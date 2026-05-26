package net.plugins.serialization.codecs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.plugins.serialization.DataResult;

import java.util.stream.Stream;

public class StreamCodec<E> implements Codec<Stream<E>> {
    private final Codec<E> codec;

    @Override
    public DataResult<JsonElement> encode(Stream<E> input) {
        try {
            JsonArray array = new JsonArray();
            for (E e : input.toList())
                array.add(codec.encode(e).getOrThrow());
            return DataResult.success(array);
        } catch (RuntimeException e) {
            return DataResult.error(e);
        }
    }

    @Override
    public DataResult<Stream<E>> decode(JsonElement element) {
        try {
            JsonArray array = element.getAsJsonArray();
            Stream.Builder<E> builder = Stream.builder();
            for (JsonElement e : array)
                builder.add(codec.decode(e).getOrThrow());
            return DataResult.success(builder.build());
        } catch (RuntimeException e) {
            return DataResult.error(e);
        }
    }

    StreamCodec(Codec<E> elementCodec) {
        this.codec = elementCodec;
    }
}
