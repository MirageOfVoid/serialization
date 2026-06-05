package net.plugins.serialization;

import net.plugins.util.RecordBuilder;

public class FieldEncoder<R> implements MapEncoder<R> {
    private final Encoder<R> encoder;
    private final String name;

    @Override
    public <T> RecordBuilder<T> encode(DynamicOps<T> ops, R input, RecordBuilder<T> prefix) {
        return prefix.add(name, encoder.encodeStart(ops, input));
    }

    @Override
    public <T> DataResult<T> compressedEncode(DynamicOps<T> ops, R input, T prefix) {
        return encoder.encode(ops, input, prefix);
    }

    public FieldEncoder(Encoder<R> encoder, String name) {
        this.encoder = encoder;
        this.name = name;
    }
}
