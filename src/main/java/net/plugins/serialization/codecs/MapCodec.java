package net.plugins.serialization.codecs;

import net.plugins.serialization.*;
import net.plugins.util.MapLike;
import net.plugins.util.Pair;
import net.plugins.util.RecordBuilder;

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
        return Codec.of(this::compressedEncode, this::compressedDecode, toString());
    }
}
