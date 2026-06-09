package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;
import net.plugins.util.MapLike;
import net.plugins.util.RecordBuilder;

import java.util.Optional;

public class OptionalFieldCodec<R> extends MapCodec<Optional<R>> {
    private final Codec<R> codec;
    private final String name;
    private final boolean successful;

    @Override
    public <T> DataResult<Optional<R>> decode(DynamicOps<T> ops, MapLike<T> input) {
        T value = input.get(name);
        if (value == null)
            return DataResult.success(Optional.empty());
        DataResult<R> parsed = codec.parse(ops, value);
        if (parsed.isError() && successful)
            return DataResult.success(Optional.empty());
        return parsed.map(Optional::of).setPartial(parsed.resultOrPartial());
    }

    @Override
    public <T> RecordBuilder<T> encode(DynamicOps<T> ops, Optional<R> input, RecordBuilder<T> prefix) {
        if (input.isPresent())
            return prefix.add(name, codec.encodeStart(ops, input.get()));
        return prefix;
    }

    public OptionalFieldCodec(Codec<R> codec, String name, boolean successful) {
        this.codec = codec;
        this.name = name;
        this.successful = successful;
    }
}
