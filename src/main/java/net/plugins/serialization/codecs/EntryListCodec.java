package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;
import net.plugins.util.MapLike;
import net.plugins.util.Pair;
import net.plugins.util.RecordBuilder;

import java.util.HashMap;
import java.util.Map;

public class EntryListCodec<K, V> implements Codec<Map<K, V>> {
    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;

    @Override
    public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMapValues(input).flatMap(stream -> {
            State<T> state = new State<>(ops);
            stream.forEach(state::apply);
            return state.build();
        });
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, Map<K, V> input, T prefix) {
        RecordBuilder<T> builder = ops.mapBuilder();
        input.forEach((key, value) -> builder.add(keyCodec.encodeStart(ops, key), valueCodec.encodeStart(ops, value)));
        return builder.build(prefix);
    }

    private final class State<T> {
        Map<K, V> map = new HashMap<>();
        DataResult<Object> result = DataResult.success(new Object());
        final Map<T, T> fails = new HashMap<>();
        final DynamicOps<T> ops;

        State(DynamicOps<T> ops) {
            this.ops = ops;
        }

        void apply(T k, T v) {
            DataResult<K> keyResult = keyCodec.parse(ops, k);
            DataResult<V> valueResult = valueCodec.parse(ops, v);

            keyResult.ifError(keyError -> valueResult.ifError(valueError -> fails.put(k, v)));
            keyResult.ifSuccess(key -> valueResult.ifSuccess(value -> map.put(key, value)));
            result = result.apply3((res, o, o1) -> res, keyResult, valueResult);
        }

        void apply(Pair<T, T> pair) {
            apply(pair.getFirst(), pair.getSecond());
        }

        DataResult<Pair<Map<K, V>, T>> build() {
            T errors = ops.createMap(MapLike.of(fails, ops));
            Pair<Map<K, V>, T> pair = Pair.of(Map.copyOf(map), errors);
            return result.map(o -> pair).setPartial(pair);
        }
    }

    public EntryListCodec(Codec<K> keyCodec, Codec<V> valueCodec) {
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
    }
}
