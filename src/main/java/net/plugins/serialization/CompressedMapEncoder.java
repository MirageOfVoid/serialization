package net.plugins.serialization;

public class CompressedMapEncoder<R> implements Encoder<R> {
    private final MapEncoder<R> encoder;

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, R input, T prefix) {
        return encoder.encode(ops, input, ops.mapBuilder()).build(prefix);
    }

    public CompressedMapEncoder(MapEncoder<R> encoder) {
        this.encoder = encoder;
    }
}
