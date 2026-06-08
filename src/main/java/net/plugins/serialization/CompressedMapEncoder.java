package net.plugins.serialization;

import net.plugins.util.RecordBuilder;

public class CompressedMapEncoder<R> implements Encoder<R> {
    private final MapEncoder<R> encoder;
//    private final String name;

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, R input, T prefix) {
//        if (result.isError())
//            return result;
//
//        DataResult<MapLike<T>> map = ops.getMap(result.getOrThrow());
//        if (map.isError())
//            return result;
//
//        T obj = map.getOrThrow().get(name);
//        return ops.mergeToMap(prefix, ops.getMap(obj).getOrThrow());
        RecordBuilder<T> builder = ops.mapBuilder();
        encoder.encode(ops, input, builder).build();
        return builder.build(prefix);
    }

    public CompressedMapEncoder(MapEncoder<R> encoder) {
        this.encoder = encoder;
//        this.name = encoder.toString();
    }
}
