package net.plugins.serialization.codecs;

import net.plugins.serialization.*;
import net.plugins.serialization.building.CodecBuilder;
import net.plugins.util.MapLike;
import net.plugins.util.Pair;
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
            public <T> DataResult<Pair<R, T>> compressedDecode(DynamicOps<T> ops, T t) {
                return decoder.compressedDecode(ops, t);
            }

            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, R input, RecordBuilder<T> prefix) {
                return encoder.encode(ops, input, prefix);
            }

            @Override
            public <T> DataResult<T> compressedEncode(DynamicOps<T> ops, R input, T prefix) {
                return encoder.compressedEncode(ops, input, prefix);
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

    public Codec<R> codec() {
        return new MapCodecCodec<>(this);
    }

    public record MapCodecCodec<R>(MapCodec<R> codec) implements Codec<R> {

        @Override
        public <T> DataResult<Pair<R, T>> decode(DynamicOps<T> ops, T t) {
            return codec.compressedDecode(ops, t);
        }

        @Override
        public <T> DataResult<T> encode(DynamicOps<T> ops, R input, T prefix) {
//            return codec.encode(ops, input, ops.mapBuilder()).build(prefix);
            return codec.compressedEncode(ops, input, prefix);
        }
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

    public <O> CodecBuilder<O, R> forGetter(Function<O, R> getter) {
        return CodecBuilder.of(this, getter);
    }
}
