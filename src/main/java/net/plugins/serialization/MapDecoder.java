package net.plugins.serialization;

import net.plugins.util.MapLike;
import net.plugins.util.Pair;

public interface MapDecoder<R> {
    <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input);

    <T> DataResult<Pair<R, T>> compressedDecode(DynamicOps<T> ops, T t);
}
