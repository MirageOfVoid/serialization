package net.plugins.serialization;

import net.plugins.util.MapLike;
import net.plugins.util.Pair;

import java.util.function.Function;

public interface MapDecoder<R> {
    <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input);

    default <U> MapDecoder<U> map(Function<R, U> mapper) {
        return new MapDecoder<U>() {
            @Override
            public <T> DataResult<U> decode(DynamicOps<T> ops, MapLike<T> input) {
                return MapDecoder.this.decode(ops, input).map(mapper);
            }
        };
    }

    default <U> MapDecoder<U> flatMap(Function<R, DataResult<U>> mapper) {
        return new MapDecoder<U>() {
            @Override
            public <T> DataResult<U> decode(DynamicOps<T> ops, MapLike<T> input) {
                return MapDecoder.this.decode(ops, input).flatMap(mapper);
            }
        };
    }

    default Decoder<R> compress() {
        return new CompressedMapDecoder<>(this);
    }
}
