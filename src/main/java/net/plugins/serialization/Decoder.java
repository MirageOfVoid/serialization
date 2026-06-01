package net.plugins.serialization;

import com.google.gson.JsonElement;
import net.plugins.serialization.ops.DynamicOps;

import java.util.function.Function;

public interface Decoder<R> {
    <T> DataResult<R> decode(DynamicOps<T> ops, T t);

    default <T> Decoder<T> map(Function<R, T> mapper) {
//        return element -> Decoder.this.decode(element).map(mapper);
        return new Decoder<T>() {
            @Override
            public <T1> DataResult<T> decode(DynamicOps<T1> ops, T1 t1) {
                return Decoder.this.decode(ops, t1).map(mapper);
            }
        };
    }

    default <T> Decoder<T> flatMap(Function<R, DataResult<T>> mapper) {
//        return element -> decode(element).flatMap(mapper);
        return new Decoder<T>() {
            @Override
            public <T1> DataResult<T> decode(DynamicOps<T1> ops, T1 t1) {
                return Decoder.this.decode(ops, t1).flatMap(mapper);
            }
        };
    }
}
