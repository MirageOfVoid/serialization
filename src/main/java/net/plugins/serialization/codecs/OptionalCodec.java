package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;
import net.plugins.util.Pair;

import java.util.Optional;

public class OptionalCodec<R> implements Codec<Optional<R>> {
    private final Codec<R> codec;

    @Override
    public <T> DataResult<Pair<Optional<R>, T>> decode(DynamicOps<T> ops, T input) {
        if (ops.isEmpty(input))
            return DataResult.success(Pair.of(Optional.empty(), input));

        return codec.decode(ops, input).map(pair -> pair.mapFirst(Optional::of));
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, Optional<R> input, T prefix) {
        if (input.isPresent())
            return codec.encode(ops, input.get(), prefix);
        return DataResult.success(prefix);
    }

    public OptionalCodec(Codec<R> codec) {
        this.codec = codec;
    }
}
