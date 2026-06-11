package net.plugins.serialization;

import net.plugins.util.MapLike;
import net.plugins.util.Pair;

import java.util.function.Function;

public interface Decoder<R> {
    <T> DataResult<Pair<R, T>> decode(DynamicOps<T> ops, T input);

    default <T> DataResult<R> parse(DynamicOps<T> ops, T input) {
        return decode(ops, input).map(Pair::getFirst);
    }

    default <U> Decoder<U> map(Function<R, U> mapper) {
        return new Decoder<U>() {
            @Override
            public <T> DataResult<Pair<U, T>> decode(DynamicOps<T> ops, T input) {
                return Decoder.this.decode(ops, input).map(pair -> pair.mapFirst(mapper));
            }
        };
    }

    default <U> Decoder<U> flatMap(Function<R, DataResult<U>> mapper) {
        return new Decoder<U>() {
            @Override
            public <T> DataResult<Pair<U, T>> decode(DynamicOps<T> ops, T input) {
                return Decoder.this.decode(ops, input).flatMap(p -> mapper.apply(p.getFirst()).map(r -> Pair.of(r, p.getSecond())));
            }
        };
    }

    default MapDecoder<R> fieldOf(String name) {
        return new FieldDecoder<>(this, name);
    }

    default Decoder<R> orElse(R r) {
        return new Decoder<R>() {
            @Override
            public <T> DataResult<Pair<R, T>> decode(DynamicOps<T> ops, T input) {
                return Decoder.this.decode(ops, input).orElse(() -> Pair.of(r, input));
            }
        };
    }

    static <R> MapDecoder<R> unit(R instance) {
        return new MapDecoder<R>() {
            @Override
            public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
                return DataResult.success(instance);
            }
        };
    }
}
