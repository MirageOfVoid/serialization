package net.plugins.serialization.building;

import net.plugins.serialization.MapDecoder;
import net.plugins.serialization.MapEncoder;
import net.plugins.serialization.codecs.Codec;
import net.plugins.serialization.codecs.MapCodec;

import java.util.function.Function;

public class CodecBuilder<O, F> {
    private final MapEncoder<F> encoder;
    private final MapDecoder<F> decoder;
    private final Function<O, F> getter;

    public MapEncoder<F> encoder() {
        return encoder;
    }

    public MapDecoder<F> decoder() {
        return decoder;
    }

    public Function<O, F> getter() {
        return getter;
    }

    CodecBuilder(MapEncoder<F> encoder, MapDecoder<F> decoder, Function<O, F> getter) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.getter = getter;
    }

    public static <O, F> CodecBuilder<O, F> of(MapCodec<F> codec, Function<O, F> getter) {
        return new CodecBuilder<>(codec, codec, getter);
    }

    public static <O> MapCodec<O> mapCodec(Function<Instance<O>, CodecBuilder<O, O>> function) {
        CodecBuilder<O, O> builder = function.apply(new Instance<>());
        return MapCodec.of(builder.encoder, builder.decoder);
    }

    public static final class Instance<O> {
        public <T1> Products.P1<O, T1> group(CodecBuilder<O, T1> t1) {
            return new Products.P1<>(t1);
        }

        public <T1, T2> Products.P2<O, T1, T2> group(CodecBuilder<O, T1> t1, CodecBuilder<O, T2> t2) {
            return new Products.P2<>(t1, t2);
        }
    }
}
