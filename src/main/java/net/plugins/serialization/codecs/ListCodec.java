package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;
import net.plugins.util.ListBuilder;
import net.plugins.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ListCodec<E> implements Codec<List<E>> {
    private final Codec<E> codec;

    @Override
    public <T> DataResult<Pair<List<E>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getList(input).flatMap(list -> {
            State<T> state = new State<>(ops);
            list.forEach(state::apply);
            return state.build();
        });
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, List<E> input, T prefix) {
        ListBuilder<T> builder = ops.listBuilder();
        for (E e : input) {
            builder.add(codec.encodeStart(ops, e));
        }
        return builder.build(prefix);
    }

    public ListCodec(Codec<E> codec) {
        this.codec = codec;
    }

    private class State<T> {
        final DynamicOps<T> ops;
        final List<E> elements = new ArrayList<>();
        final List<T> fails = new ArrayList<>();
        DataResult<Object> result = DataResult.success(new Object());

        private State(DynamicOps<T> ops) {
            this.ops = ops;
        }

        void apply(T element) {
            DataResult<Pair<E, T>> elementResult = codec.decode(ops, element);
            elementResult.ifError(error -> fails.add(element));
            elementResult.ifSuccess(pair -> elements.add(pair.getFirst()));
            result = result.apply2((res, e) -> res, elementResult);
        }

        DataResult<Pair<List<E>, T>> build() {
            T errors = ops.createList(fails);
            Pair<List<E>, T> pair = Pair.of(List.copyOf(elements), errors);
            return result.map(o -> pair).setPartial(pair);
        }
    }

    @Override
    public String toString() {
        return codec + "[list]";
    }
}
