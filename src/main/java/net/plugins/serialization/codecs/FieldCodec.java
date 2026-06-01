package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.ops.DynamicOps;

import java.util.HashMap;
import java.util.Map;

public class FieldCodec<R> implements Codec<R> {
    private final Codec<R> codec;
    private final String name;

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, R input) {
        Map<String, T> map = new HashMap<>();
        DataResult<T> result = codec.encode(ops, input);

        if (result.isError())
            return result;

        map.put(name, result.getOrThrow());
        return DataResult.success(ops.createMap(map));
    }

    @Override
    public <T> DataResult<R> decode(DynamicOps<T> ops, T input) {
        DataResult<Map<String, T>> in = ops.getMap(input);
        if (in.isError())
            return in.error().get().cast();

        Map<String, T> map = in.getOrThrow();

        if (!map.containsKey(name))
            return DataResult.error("(" + this + ") Could not find field '" + name + "'");

        T t = map.get(name);

        return codec.decode(ops, t);
    }

    FieldCodec(String name, Codec<R> codec) {
        this.name = name;
        this.codec = codec;
    }

    @Override
    public String toString() {
        return codec + "[field:" + name + "]";
    }

    public String getName() {
        return name;
    }
}
