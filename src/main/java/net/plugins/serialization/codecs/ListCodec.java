package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;
import net.plugins.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ListCodec<E> implements Codec<List<E>> {
    private final Codec<E> codec;

    @Override
    public <T> DataResult<Pair<List<E>, T>> decode(DynamicOps<T> ops, T t) {
        return ops.getList(t).flatMap(list -> {
            State<T> state = new State<>(ops);
            list.forEach(state::add);
            return state.build();
        });
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, List<E> input, T prefix) {
        DynamicOps.ListBuilder<T> builder = ops.listBuilder();
        for (E e : input) {
            if (!builder.add(codec.encodeStart(ops, e))) {
                return DataResult.error(() -> this + ": encoding list failed", builder.build());
            }
        }
        return DataResult.success(builder.build());
    }

    public ListCodec(Codec<E> codec) {
        this.codec = codec;
    }

    private class State<T> {
        private final DynamicOps<T> ops;
        private final List<E> elements = new ArrayList<>();
        private final DynamicOps.ListBuilder<T> fails;

        private State(DynamicOps<T> ops) {
            this.ops = ops;
            this.fails = ops.listBuilder();
        }

        public void add(T element) {
            DataResult<Pair<E, T>> result = codec.decode(ops, element);
            result.ifError(ignored -> fails.add(element));
            result.resultOrPartial().ifPresent(pair -> elements.add(pair.getFirst()));
        }

        public DataResult<Pair<List<E>, T>> build() {
            if (fails.isEmpty()) {
                Pair<List<E>, T> pair = Pair.of(elements, ops.empty());
                return DataResult.success(pair);
            }
            T errors = fails.build();
            Pair<List<E>, T> pair = Pair.of(elements, errors);
            return DataResult.error(() -> this + ": decoding list failed", pair);
        }
    }

    @Override
    public String toString() {
        return codec + "[list]";
    }
}
