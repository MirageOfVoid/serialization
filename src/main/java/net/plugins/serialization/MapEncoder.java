package net.plugins.serialization;

import net.plugins.serialization.codecs.Codec;
import net.plugins.util.RecordBuilder;

import java.util.function.Function;

public interface MapEncoder<R> {
    <T> RecordBuilder<T> encode(DynamicOps<T> ops, R input, RecordBuilder<T> prefix);

    default <U> MapEncoder<U> comap(Function<U, R> mapper) {
        return new MapEncoder<U>() {
            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, U input, RecordBuilder<T> prefix) {
                return MapEncoder.this.encode(ops, mapper.apply(input), prefix);
            }
        };
    }

    default <U> MapEncoder<U> flatComap(Function<U, DataResult<R>> mapper) {
        return new MapEncoder<U>() {
            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, U input, RecordBuilder<T> prefix) {
                DataResult<R> result = mapper.apply(input);
                RecordBuilder<T> builder = prefix.withErrorsFrom(result);
                return result.map(r -> MapEncoder.this.encode(ops, r, builder)).result().orElse(builder);
            }
        };
    }

    default Encoder<R> compress() {
        return new CompressedMapEncoder<>(this);
    }
}
