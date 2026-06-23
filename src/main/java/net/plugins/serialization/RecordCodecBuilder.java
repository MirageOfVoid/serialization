package net.plugins.serialization;

import net.plugins.kinds.App;
import net.plugins.kinds.Applicative;
import net.plugins.kinds.K1;
import net.plugins.util.function.Function3;
import net.plugins.util.function.Function4;
import net.plugins.serialization.codecs.Codec;
import net.plugins.serialization.codecs.MapCodec;
import net.plugins.util.MapLike;
import net.plugins.util.RecordBuilder;
import net.plugins.util.function.Function5;

import java.util.function.BiFunction;
import java.util.function.Function;

public class RecordCodecBuilder<O, F> implements App<RecordCodecBuilder.Mu<O>, F> {
    public static final class Mu<O> implements K1 {}

    public static <O, F> RecordCodecBuilder<O, F> unbox(App<Mu<O>, F> box) {
        return  (RecordCodecBuilder<O, F>) box;
    }

    private final Function<O, F> getter;
    private final Function<O, MapEncoder<F>> encoder;
    private final MapDecoder<F> decoder;

    private RecordCodecBuilder(Function<O, F> getter, Function<O, MapEncoder<F>> encoder, MapDecoder<F> decoder) {
        this.getter = getter;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public static <O> Instance<O> instance() {
        return new Instance<>();
    }

    public static <O, F> RecordCodecBuilder<O, F> of(Function<O, F> getter, MapCodec<F> codec) {
        return new RecordCodecBuilder<>(getter, o -> codec, codec);
    }

    public static <O, F> RecordCodecBuilder<O, F> point(F instance) {
        return new RecordCodecBuilder<>(o -> instance, o -> Encoder.empty(), Decoder.unit(instance));
    }

    public static <O> MapCodec<O> mapCodec(Function<Instance<O>, ? extends App<Mu<O>, O>> builder, String name) {
        return build(builder.apply(instance()), name);
    }

    public static <O> MapCodec<O> mapCodec(Function<Instance<O>, ? extends App<Mu<O>, O>> builder) {
        return build(builder.apply(instance()), "RecordMapCodec");
    }

    public static <O> Codec<O> codec(Function<Instance<O>, ? extends App<Mu<O>, O>> builder, String name) {
        return build(builder.apply(instance()), name).compress();
    }

    public static <O> Codec<O> codec(Function<Instance<O>, ? extends App<Mu<O>, O>> builder) {
        return build(builder.apply(instance()), "RecordCodec").compress();
    }

    public static <O> MapCodec<O> build(App<Mu<O>, O> builderBox, String name) {
        RecordCodecBuilder<O, O> builder = unbox(builderBox);
        return new MapCodec<O>() {
            @Override
            public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
                return builder.decoder.decode(ops, input);
            }

            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, O input, RecordBuilder<T> prefix) {
                return builder.encoder.apply(input).encode(ops, input, prefix);
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    public static final class Instance<O> implements Applicative<Mu<O>, Instance.Mu<O>> {
        @Override
        public <A> App<RecordCodecBuilder.Mu<O>, A> point(A a) {
            return RecordCodecBuilder.point(a);
        }

        @Override
        public <T, R> App<RecordCodecBuilder.Mu<O>, R> map(Function<T, R> function, App<RecordCodecBuilder.Mu<O>, T> app) {
            RecordCodecBuilder<O, T> unbox = unbox(app);
            Function<O, T> getter = unbox.getter;
            return new RecordCodecBuilder<>(getter.andThen(function),
                    o -> new MapEncoder<R>() {
                        private final MapEncoder<T> encoder = unbox.encoder.apply(o);

                        @Override
                        public <U> RecordBuilder<U> encode(DynamicOps<U> ops, R input, RecordBuilder<U> prefix) {
                            return encoder.encode(ops, getter.apply(o), prefix);
                        }
                    },
                    unbox.decoder.map(function)
            );
        }

        @Override
        public <A, R> Function<App<RecordCodecBuilder.Mu<O>, A>, App<RecordCodecBuilder.Mu<O>, R>> lift(App<RecordCodecBuilder.Mu<O>, Function<A, R>> function) {
            return fa -> {
                RecordCodecBuilder<O, Function<A, R>> f = unbox(function);
                RecordCodecBuilder<O, A> a = unbox(fa);
                return new RecordCodecBuilder<>(
                        o -> f.getter.apply(o).apply(a.getter.apply(o)),
                        o -> {
                            MapEncoder<Function<A, R>> fEnc = f.encoder.apply(o);
                            MapEncoder<A> aEnc = a.encoder.apply(o);
                            A aFromO = a.getter.apply(o);
                            return new MapEncoder<R>() {
                                @Override
                                public <T> RecordBuilder<T> encode(DynamicOps<T> ops, R input, RecordBuilder<T> prefix) {
                                    aEnc.encode(ops, aFromO, prefix);
                                    fEnc.encode(ops, a1 -> input, prefix);
                                    return prefix;
                                }
                            };
                        },
                        new MapDecoder<R>() {
                            @Override
                            public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
                                return a.decoder.decode(ops, input).flatMap(ar ->
                                        f.decoder.decode(ops, input).map(fr -> fr.apply(ar))
                                );
                            }
                        }
                );
            };
        }

        @Override
        public <A, B, R> App<RecordCodecBuilder.Mu<O>, R> ap2(App<RecordCodecBuilder.Mu<O>, BiFunction<A, B, R>> func, App<RecordCodecBuilder.Mu<O>, A> a, App<RecordCodecBuilder.Mu<O>, B> b) {
            RecordCodecBuilder<O, BiFunction<A, B, R>> function = unbox(func);
            RecordCodecBuilder<O, A> fa = unbox(a);
            RecordCodecBuilder<O, B> fb = unbox(b);

            return new RecordCodecBuilder<>(
                    o -> function.getter.apply(o).apply(fa.getter.apply(o), fb.getter.apply(o)),
                    o -> {
                        MapEncoder<BiFunction<A, B, R>> fEncoder = function.encoder.apply(o);
                        MapEncoder<A> aEncoder = fa.encoder.apply(o);
                        A aFromO = fa.getter.apply(o);
                        MapEncoder<B> bEncoder = fb.encoder.apply(o);
                        B bFromO = fb.getter.apply(o);

                        return new MapEncoder<R>() {
                            @Override
                            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, R input, RecordBuilder<T> prefix) {
                                aEncoder.encode(ops, aFromO, prefix);
                                bEncoder.encode(ops, bFromO, prefix);
                                fEncoder.encode(ops, (a1, a2) -> input, prefix);
                                return prefix;
                            }
                        };
                    },
                    new MapDecoder<R>() {
                        @Override
                        public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
                            return DataResult.unbox(DataResult.instance().ap2(
                                    function.decoder.decode(ops, input),
                                    fa.decoder.decode(ops, input),
                                    fb.decoder.decode(ops, input)
                            ));
                        }
                    }
            );
        }

        @Override
        public <A, B, C, R> App<RecordCodecBuilder.Mu<O>, R> ap3(App<RecordCodecBuilder.Mu<O>, Function3<A, B, C, R>> func, App<RecordCodecBuilder.Mu<O>, A> a, App<RecordCodecBuilder.Mu<O>, B> b, App<RecordCodecBuilder.Mu<O>, C> c) {
            RecordCodecBuilder<O, Function3<A, B, C, R>> function = unbox(func);
            RecordCodecBuilder<O, A> fa = unbox(a);
            RecordCodecBuilder<O, B> fb = unbox(b);
            RecordCodecBuilder<O, C> fc = unbox(c);

            return new RecordCodecBuilder<>(
                    o -> function.getter.apply(o).apply(fa.getter.apply(o), fb.getter.apply(o), fc.getter.apply(o)),
                    o -> {
                        MapEncoder<Function3<A, B, C, R>> fEncoder = function.encoder.apply(o);
                        MapEncoder<A> aEncoder = fa.encoder.apply(o);
                        A aFromO = fa.getter.apply(o);
                        MapEncoder<B> bEncoder = fb.encoder.apply(o);
                        B bFromO = fb.getter.apply(o);
                        MapEncoder<C> cEncoder = fc.encoder.apply(o);
                        C cFromO = fc.getter.apply(o);

                        return new MapEncoder<R>() {
                            @Override
                            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, R input, RecordBuilder<T> prefix) {
                                aEncoder.encode(ops, aFromO, prefix);
                                bEncoder.encode(ops, bFromO, prefix);
                                cEncoder.encode(ops, cFromO, prefix);
                                fEncoder.encode(ops, (a1, a2, a3) -> input, prefix);
                                return prefix;
                            }
                        };
                    },
                    new MapDecoder<R>() {
                        @Override
                        public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
                            return DataResult.unbox(DataResult.instance().ap3(
                                    function.decoder.decode(ops, input),
                                    fa.decoder.decode(ops, input),
                                    fb.decoder.decode(ops, input),
                                    fc.decoder.decode(ops, input)
                            ));
                        }
                    }
            );
        }

        @Override
        public <A, B, C, D, R> App<RecordCodecBuilder.Mu<O>, R> ap4(App<RecordCodecBuilder.Mu<O>, Function4<A, B, C, D, R>> func, App<RecordCodecBuilder.Mu<O>, A> a, App<RecordCodecBuilder.Mu<O>, B> b, App<RecordCodecBuilder.Mu<O>, C> c, App<RecordCodecBuilder.Mu<O>, D> d) {
            RecordCodecBuilder<O, Function4<A, B, C, D, R>> function = unbox(func);
            RecordCodecBuilder<O, A> fa = unbox(a);
            RecordCodecBuilder<O, B> fb = unbox(b);
            RecordCodecBuilder<O, C> fc = unbox(c);
            RecordCodecBuilder<O, D> fd = unbox(d);

            return new RecordCodecBuilder<>(
                    o -> function.getter.apply(o).apply(fa.getter.apply(o), fb.getter.apply(o), fc.getter.apply(o), fd.getter.apply(o)),
                    o -> {
                        MapEncoder<Function4<A, B, C, D, R>> fEncoder = function.encoder.apply(o);
                        MapEncoder<A> aEncoder = fa.encoder.apply(o);
                        A aFromO = fa.getter.apply(o);
                        MapEncoder<B> bEncoder = fb.encoder.apply(o);
                        B bFromO = fb.getter.apply(o);
                        MapEncoder<C> cEncoder = fc.encoder.apply(o);
                        C cFromO = fc.getter.apply(o);
                        MapEncoder<D> dEncoder = fd.encoder.apply(o);
                        D dFromO = fd.getter.apply(o);

                        return new MapEncoder<R>() {
                            @Override
                            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, R input, RecordBuilder<T> prefix) {
                                aEncoder.encode(ops, aFromO, prefix);
                                bEncoder.encode(ops, bFromO, prefix);
                                cEncoder.encode(ops, cFromO, prefix);
                                dEncoder.encode(ops, dFromO, prefix);
                                fEncoder.encode(ops, (a1, a2, a3, a4) -> input, prefix);
                                return prefix;
                            }
                        };
                    },
                    new MapDecoder<R>() {
                        @Override
                        public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
                            return DataResult.unbox(DataResult.instance().ap4(
                                    function.decoder.decode(ops, input),
                                    fa.decoder.decode(ops, input),
                                    fb.decoder.decode(ops, input),
                                    fc.decoder.decode(ops, input),
                                    fd.decoder.decode(ops, input)
                            ));
                        }
                    }
            );
        }

        @Override
        public <A, B, C, D, E, R> App<RecordCodecBuilder.Mu<O>, R> ap5(App<RecordCodecBuilder.Mu<O>, Function5<A, B, C, D, E, R>> func, App<RecordCodecBuilder.Mu<O>, A> a, App<RecordCodecBuilder.Mu<O>, B> b, App<RecordCodecBuilder.Mu<O>, C> c, App<RecordCodecBuilder.Mu<O>, D> d, App<RecordCodecBuilder.Mu<O>, E> e) {
            RecordCodecBuilder<O, Function5<A, B, C, D, E, R>> function = unbox(func);
            RecordCodecBuilder<O, A> fa = unbox(a);
            RecordCodecBuilder<O, B> fb = unbox(b);
            RecordCodecBuilder<O, C> fc = unbox(c);
            RecordCodecBuilder<O, D> fd = unbox(d);
            RecordCodecBuilder<O, E> fe = unbox(e);

            return new RecordCodecBuilder<>(
                    o -> function.getter.apply(o).apply(fa.getter.apply(o), fb.getter.apply(o), fc.getter.apply(o), fd.getter.apply(o), fe.getter.apply(o)),
                    o -> {
                        MapEncoder<Function5<A, B, C, D, E, R>> fEncoder = function.encoder.apply(o);
                        MapEncoder<A> aEncoder = fa.encoder.apply(o);
                        A aFromO = fa.getter.apply(o);
                        MapEncoder<B> bEncoder = fb.encoder.apply(o);
                        B bFromO = fb.getter.apply(o);
                        MapEncoder<C> cEncoder = fc.encoder.apply(o);
                        C cFromO = fc.getter.apply(o);
                        MapEncoder<D> dEncoder = fd.encoder.apply(o);
                        D dFromO = fd.getter.apply(o);
                        MapEncoder<E> eEncoder = fe.encoder.apply(o);
                        E eFromO = fe.getter.apply(o);

                        return new MapEncoder<R>() {
                            @Override
                            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, R input, RecordBuilder<T> prefix) {
                                aEncoder.encode(ops, aFromO, prefix);
                                bEncoder.encode(ops, bFromO, prefix);
                                cEncoder.encode(ops, cFromO, prefix);
                                dEncoder.encode(ops, dFromO, prefix);
                                eEncoder.encode(ops, eFromO, prefix);
                                fEncoder.encode(ops, (a1, a2, a3, a4, a5) -> input, prefix);
                                return prefix;
                            }
                        };
                    },
                    new MapDecoder<R>() {
                        @Override
                        public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
                            return DataResult.unbox(DataResult.instance().ap5(
                                    function.decoder.decode(ops, input),
                                    fa.decoder.decode(ops, input),
                                    fb.decoder.decode(ops, input),
                                    fc.decoder.decode(ops, input),
                                    fd.decoder.decode(ops, input),
                                    fe.decoder.decode(ops, input)
                            ));
                        }
                    }
            );
        }

        public static final class Mu<O> implements Applicative.Mu {}
    }
}
