package net.plugins.serialization;

import net.plugins.util.RecordBuilder;

import java.util.function.Function;

public interface Encoder<R> {
    <T> DataResult<T> encode(DynamicOps<T> ops, R input, T prefix);

    default <T> DataResult<T> encodeStart(DynamicOps<T> ops, R input) {
        return encode(ops, input, ops.empty());
    }

    default <U> Encoder<U> comap(Function<U, R> mapper) {
        return new Encoder<U>() {
            @Override
            public <T> DataResult<T> encode(DynamicOps<T> ops, U input, T prefix) {
                return Encoder.this.encode(ops, mapper.apply(input), prefix);
            }
        };
    }

    default <U> Encoder<U> flatComap(Function<U, DataResult<R>> mapper) {
        return new Encoder<U>() {
            @Override
            public <T> DataResult<T> encode(DynamicOps<T> ops, U input, T prefix) {
                return mapper.apply(input).flatMap(r -> Encoder.this.encode(ops, r, prefix));
            }
        };
    }

    default MapEncoder<R> fieldOf(String name) {
        return new MapEncoder<R>() {
            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, R input, RecordBuilder<T> prefix) {
                return prefix.add(name, Encoder.this.encodeStart(ops, input));
            }

            @Override
            public <T> DataResult<T> compressedEncode(DynamicOps<T> ops, R input, T prefix) {
                return Encoder.this.encode(ops, input, prefix);
            }
        };
    }
}
