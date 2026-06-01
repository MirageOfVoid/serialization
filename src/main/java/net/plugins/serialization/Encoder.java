package net.plugins.serialization;

import com.google.gson.JsonElement;
import net.plugins.serialization.ops.DynamicOps;

import java.util.function.Function;

public interface Encoder<R> {
    <T> DataResult<T> encode(DynamicOps<T> ops, R input);

    default <T> Encoder<T> comap(Function<T, R> mapper) {
//        return input -> encode(mapper.apply(input));
        return new Encoder<T>() {
            @Override
            public <T1> DataResult<T1> encode(DynamicOps<T1> ops, T input) {
                return Encoder.this.encode(ops, mapper.apply(input));
            }
        };
    }

    default <T> Encoder<T> flatComap(Function<T, DataResult<R>> mapper) {
//        return input -> mapper.apply(input).flatMap(this::encode);
        return new Encoder<T>() {
            @Override
            public <T1> DataResult<T1> encode(DynamicOps<T1> ops, T input) {
                return mapper.apply(input).flatMap(r -> Encoder.this.encode(ops, r));
            }
        };
    }
}
