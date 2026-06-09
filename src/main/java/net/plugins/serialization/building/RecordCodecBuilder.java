package net.plugins.serialization.building;

import net.plugins.serialization.MapDecoder;
import net.plugins.serialization.MapEncoder;
import net.plugins.serialization.codecs.Codec;
import net.plugins.serialization.codecs.MapCodec;

import java.util.function.Function;

public class RecordCodecBuilder<O, F> {
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

    RecordCodecBuilder(MapEncoder<F> encoder, MapDecoder<F> decoder, Function<O, F> getter) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.getter = getter;
    }

    public static <O, F> RecordCodecBuilder<O, F> of(MapCodec<F> codec, Function<O, F> getter) {
        return new RecordCodecBuilder<>(codec, codec, getter);
    }

    public static <O> MapCodec<O> mapCodec(Function<Instance<O>, RecordCodecBuilder<O, O>> function, String name) {
        return build(function.apply(instance()), name);
    }

    public static <O> MapCodec<O> mapCodec(Function<Instance<O>, RecordCodecBuilder<O, O>> function) {
        return build(function.apply(instance()));
    }

    public static <O> Codec<O> codec(Function<Instance<O>, RecordCodecBuilder<O, O>> function, String name) {
        return build(function.apply(instance()), name).compress();
    }

    public static <O> Codec<O> codec(Function<Instance<O>, RecordCodecBuilder<O, O>> function) {
        return build(function.apply(instance())).compress();
    }

    public static <O> MapCodec<O> build(RecordCodecBuilder<O, O> builder, String name) {
        return MapCodec.of(builder.encoder, builder.decoder, name);
    }

    public static <O> MapCodec<O> build(RecordCodecBuilder<O, O> builder) {
        return MapCodec.of(builder.encoder, builder.decoder);
    }

    public static <O> Instance<O> instance() {
        return new Instance<>();
    }

    public static final class Instance<O> {
        public <T1> Products.P1<O, T1> group(RecordCodecBuilder<O, T1> t1) {
            return new Products.P1<>(t1);
        }
        public <T1, T2> Products.P2<O, T1, T2> group(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2) {
            return new Products.P2<>(t1, t2);
        }
        public <T1, T2, T3, T4> Products.P3<O, T1, T2, T3> group(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3) {
            return new Products.P3<>(t1, t2, t3);
        }
        public <T1, T2, T3, T4> Products.P4<O, T1, T2, T3, T4> group(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4) {
            return new Products.P4<>(t1, t2, t3, t4);
        }
        public <T1, T2, T3, T4, T5> Products.P5<O, T1, T2, T3, T4, T5> group(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4, RecordCodecBuilder<O, T5> t5) {
            return new Products.P5<>(t1, t2, t3, t4, t5);
        }
        public <T1, T2, T3, T4, T5, T6> Products.P6<O, T1, T2, T3, T4, T5, T6> group(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4, RecordCodecBuilder<O, T5> t5, RecordCodecBuilder<O, T6> t6) {
            return new Products.P6<>(t1, t2, t3, t4, t5, t6);
        }
        public <T1, T2, T3, T4, T5, T6, T7> Products.P7<O, T1, T2, T3, T4, T5, T6, T7> group(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4, RecordCodecBuilder<O, T5> t5, RecordCodecBuilder<O, T6> t6, RecordCodecBuilder<O, T7> t7) {
            return new Products.P7<>(t1, t2, t3, t4, t5, t6, t7);
        }
        public <T1, T2, T3, T4, T5, T6, T7, T8> Products.P8<O, T1, T2, T3, T4, T5, T6, T7, T8> group(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4, RecordCodecBuilder<O, T5> t5, RecordCodecBuilder<O, T6> t6, RecordCodecBuilder<O, T7> t7, RecordCodecBuilder<O, T8> t8) {
            return new Products.P8<>(t1, t2, t3, t4, t5, t6, t7, t8);
        }
    }
}
