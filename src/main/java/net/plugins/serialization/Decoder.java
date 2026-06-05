package net.plugins.serialization;

import net.plugins.util.MapLike;
import net.plugins.util.Pair;

import java.util.function.Function;

public interface Decoder<R> {
    <T> DataResult<Pair<R, T>> decode(DynamicOps<T> ops, T t);

    default <T> DataResult<R> parse(DynamicOps<T> ops, T t) {
        return decode(ops, t).map(Pair::getFirst);
    }

    default <U> Decoder<U> map(Function<R, U> mapper) {
        return new Decoder<U>() {
            @Override
            public <T> DataResult<Pair<U, T>> decode(DynamicOps<T> ops, T t) {
                return Decoder.this.decode(ops, t).map(pair -> pair.mapFirst(mapper));
            }
        };
    }

    default <U> Decoder<U> flatMap(Function<R, DataResult<U>> mapper) {
        return new Decoder<U>() {
            @Override
            public <T> DataResult<Pair<U, T>> decode(DynamicOps<T> ops, T t) {
                return Decoder.this.decode(ops, t).flatMap(p -> mapper.apply(p.getFirst()).map(r -> Pair.of(r, p.getSecond())));
            }
        };
    }

    default MapDecoder<R> fieldOf(String name) {
        return new FieldDecoder<>(this, name);
    }

    default Decoder<R> orElse(R r) {
        return new Decoder<R>() {
            @Override
            public <T> DataResult<Pair<R, T>> decode(DynamicOps<T> ops, T t) {
                return Decoder.this.decode(ops, t).orElse(() -> Pair.of(r, t));
            }
        };
    }
}
