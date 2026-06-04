package net.plugins.serialization;

import net.plugins.util.RecordBuilder;

public interface MapEncoder<R> {
    <T> RecordBuilder<T> encode(DynamicOps<T> ops, R input, RecordBuilder<T> prefix);

    <T> DataResult<T> compressedEncode(DynamicOps<T> ops, R input, T prefix);
}
