package net.plugins.serialization.codecs;

import net.plugins.serialization.*;
import net.plugins.serialization.RecordCodecBuilder;
import net.plugins.util.MapLike;
import net.plugins.util.RecordBuilder;

import java.util.function.Function;

public abstract class MapCodec<R> implements MapEncoder<R>, MapDecoder<R> {
    public static <R> MapCodec<R> of(MapEncoder<R> encoder, MapDecoder<R> decoder, String name) {
        return new MapCodec<R>() {
            @Override
            public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
                return decoder.decode(ops, input);
            }

            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, R input, RecordBuilder<T> prefix) {
                return encoder.encode(ops, input, prefix);
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    public static <R> MapCodec<R> of(MapEncoder<R> encoder, MapDecoder<R> decoder) {
        return of(encoder, decoder, "MapCodec");
    }

    public Codec<R> compress() {
        return Codec.of(
                MapEncoder.super.compress(),
                MapDecoder.super.compress(),
                toString()
        );
    }

    public <U> MapCodec<U> xmap(Function<R, U> to, Function<U, R> from) {
        return MapCodec.of(comap(from), map(to), toString() + "[xmapped]");
    }

    public <U> MapCodec<U> flatComapMap(Function<R, U> to, Function<U, DataResult<R>> from) {
        return MapCodec.of(flatComap(from), map(to), toString() + "[flatComapMapped]");
    }

    public <U> MapCodec<U> comapFlatMap(Function<R, DataResult<U>> to, Function<U, R> from) {
        return MapCodec.of(comap(from), flatMap(to), toString() + "[comapFlatMapped]");
    }

    public <U> MapCodec<U> flatXmap(Function<R, DataResult<U>> to, Function<U, DataResult<R>> from) {
        return MapCodec.of(flatComap(from), flatMap(to), toString() + "[flatXmapped]");
    }

    public MapCodec<R> validate(Function<R, DataResult<R>> checker) {
        return flatXmap(checker, checker);
    }

    public <O> RecordCodecBuilder<O, R> forGetter(Function<O, R> getter) {
        return RecordCodecBuilder.of(getter, this);
    }

    public MapCodec<R> orElse(R defaultValue) {
        return MapCodec.of(
                this,
                MapDecoder.super.orElse(defaultValue),
                toString()
        );
    }

    public MapCodec<R> fieldOf(String name) {
        return compress().fieldOf(name);
    }
}
