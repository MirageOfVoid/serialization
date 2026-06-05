package net.plugins.serialization;

import net.plugins.util.MapLike;
import net.plugins.util.Pair;

import java.util.function.Function;

public interface MapDecoder<R> {
    <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input);

    <T> DataResult<Pair<R, T>> compressedDecode(DynamicOps<T> ops, T t);

//    default <T> DataResult<Pair<R, T>> compressedDecode(DynamicOps<T> ops, T t) {
//        return ops.getMap(t).flatMap(map -> decode(ops, map)).map(r -> Pair.of(r, t));
//    }

    default <U> MapDecoder<U> map(Function<R, U> mapper) {
        return new MapDecoder<U>() {
            @Override
            public <T> DataResult<U> decode(DynamicOps<T> ops, MapLike<T> input) {
                return MapDecoder.this.decode(ops, input).map(mapper);
            }

            @Override
            public <T> DataResult<Pair<U, T>> compressedDecode(DynamicOps<T> ops, T t) {
                return MapDecoder.this.compressedDecode(ops, t).map(pair -> pair.mapFirst(mapper));
            }
        };
    }

    default <U> MapDecoder<U> flatMap(Function<R, DataResult<U>> mapper) {
        return new MapDecoder<U>() {
            @Override
            public <T> DataResult<U> decode(DynamicOps<T> ops, MapLike<T> input) {
                return MapDecoder.this.decode(ops, input).flatMap(mapper);
            }

            @Override
            public <T> DataResult<Pair<U, T>> compressedDecode(DynamicOps<T> ops, T t) {
                return MapDecoder.this.compressedDecode(ops, t).flatMap(pair -> mapper.apply(pair.getFirst()).map(u -> Pair.of(u, pair.getSecond())));
            }
        };
    }
}
