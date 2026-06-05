package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;
import net.plugins.util.Pair;

public interface PrimitiveCodec<R> extends Codec<R> {
    <T> T write(DynamicOps<T> ops, R input);

    <T> DataResult<R> read(DynamicOps<T> ops, T input);

    @Override
    default <T> DataResult<T> encode(DynamicOps<T> ops, R input, T prefix) {
        return DataResult.success(write(ops, input));
    }

    @Override
    default <T> DataResult<Pair<R, T>> decode(DynamicOps<T> ops, T t) {
        return read(ops, t).map(r -> Pair.of(r, ops.empty()));
    }
}
