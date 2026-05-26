package net.plugins.serialization;

import com.google.gson.JsonElement;

import java.util.function.Function;

public interface Decoder<R> {
    DataResult<R> decode(JsonElement element);

    default <T> Decoder<T> map(Function<R, T> mapper) {
        return element -> Decoder.this.decode(element).map(mapper);
    }

    default <T> Decoder<T> flatMap(Function<R, DataResult<T>> mapper) {
        return element -> decode(element).flatMap(mapper);
    }
}
