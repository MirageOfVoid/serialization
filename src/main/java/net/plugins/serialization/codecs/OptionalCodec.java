package net.plugins.serialization.codecs;

import com.google.gson.JsonElement;
import net.plugins.serialization.DataResult;

import java.util.List;
import java.util.Optional;

public class OptionalCodec<R> implements Codec<Optional<R>> {
    private final Codec<R> codec;

    @Override
    public DataResult<JsonElement> encode(Optional<R> input) {
        if (input.isEmpty())
            return DataResult.error("(%s) Null value input".formatted(this));
        return codec.encode(input.get());
    }

    @Override
    public DataResult<Optional<R>> decode(JsonElement element) {
        if (element == null || element.isJsonNull())
            return DataResult.error("(%s) Null json input".formatted(this));
        return codec.decode(element).map(Optional::of);
    }

    OptionalCodec(Codec<R> codec) {
        this.codec = codec;
    }

    @Override
    public String toString() {
        return codec + "[optional]";
    }
}
