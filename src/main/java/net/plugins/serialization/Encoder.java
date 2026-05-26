package net.plugins.serialization;

import com.google.gson.JsonElement;

import java.util.function.Function;

public interface Encoder<R> {
    DataResult<JsonElement> encode(R input);

    default <T> Encoder<T> comap(Function<T, R> mapper) {
        return input -> encode(mapper.apply(input));
    }

    default <T> Encoder<T> flatComap(Function<T, DataResult<R>> mapper) {
        return input -> mapper.apply(input).flatMap(this::encode);
    }
}
