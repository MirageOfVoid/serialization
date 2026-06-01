package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.ops.DynamicOps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapCodec<E> implements Codec<Map<String, E>> {
    private final Codec<E> codec;

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, Map<String, E> input) {
        DynamicOps.MapBuilder<T> builder = ops.mapBuilder();
        for (String key : input.keySet()) {
            try {
                builder.add(key, codec.encode(ops, input.get(key)).getOrThrow());
            } catch (RuntimeException e) {
                return DataResult.error(() -> "(" + this + "): " + e.getMessage(), builder.build());
            }
        }
        return DataResult.success(builder.build());
    }

    @Override
    public <T> DataResult<Map<String, E>> decode(DynamicOps<T> ops, T t) {
        Map<String, T> map;

        try {
            map = ops.getMap(t).getOrThrow();
        } catch (RuntimeException e) {
            return DataResult.error(e);
        }

        Map<String, E> result = new HashMap<>();

        for (String key : map.keySet()) {
            try {
                result.put(key, codec.decode(ops, map.get(key)).getOrThrow());
            } catch (RuntimeException e) {
                return DataResult.error(() -> "(" + this + ") " + e.getMessage(), result);
            }
        }

        return DataResult.success(result);
    }

    MapCodec(Codec<E> codec) {
        this.codec = codec;
    }

    @Override
    public String toString() {
        return codec + "[map]";
    }
}
