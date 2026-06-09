package net.plugins.serialization.building;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;
import net.plugins.serialization.MapDecoder;
import net.plugins.serialization.MapEncoder;
import net.plugins.util.MapLike;
import net.plugins.util.RecordBuilder;
import net.plugins.util.function.*;

import java.util.function.BiFunction;
import java.util.function.Function;

interface Products {
    record P1<O, T1>(RecordCodecBuilder<O, T1> t1) {
        public RecordCodecBuilder<O, O> apply(Function<T1, O> function) {
            return ap1(t1, function);
        }
    }
    record P2<O, T1, T2>(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2) {
        public RecordCodecBuilder<O, O> apply(BiFunction<T1, T2, O> function) {
            return ap2(t1, t2, function);
        }
    }
    record P3<O, T1, T2, T3>(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3) {
        public RecordCodecBuilder<O, O> apply(Function3<T1, T2, T3, O> function) {
            return ap3(t1, t2, t3, function);
        }
    }
    record P4<O, T1, T2, T3, T4>(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4) {
        public RecordCodecBuilder<O, O> apply(Function4<T1, T2, T3, T4, O> function) {
            return ap4(t1, t2, t3, t4, function);
        }
    }
    record P5<O, T1, T2, T3, T4, T5>(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4, RecordCodecBuilder<O, T5> t5) {
        public RecordCodecBuilder<O, O> apply(Function5<T1, T2, T3, T4, T5, O> function) {
            return ap5(t1, t2, t3, t4, t5, function);
        }
    }
    record P6<O, T1, T2, T3, T4, T5, T6>(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4, RecordCodecBuilder<O, T5> t5, RecordCodecBuilder<O, T6> t6) {
        public RecordCodecBuilder<O, O> apply(Function6<T1, T2, T3, T4, T5, T6, O> function) {
            return ap6(t1, t2, t3, t4, t5, t6, function);
        }
    }
    record P7<O, T1, T2, T3, T4, T5, T6, T7>(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4, RecordCodecBuilder<O, T5> t5, RecordCodecBuilder<O, T6> t6, RecordCodecBuilder<O, T7> t7) {
        public RecordCodecBuilder<O, O> apply(Function7<T1, T2, T3, T4, T5, T6, T7, O> function) {
            return ap7(t1, t2, t3, t4, t5, t6, t7, function);
        }
    }
    record P8<O, T1, T2, T3, T4, T5, T6, T7, T8>(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4, RecordCodecBuilder<O, T5> t5, RecordCodecBuilder<O, T6> t6, RecordCodecBuilder<O, T7> t7, RecordCodecBuilder<O, T8> t8) {
        public RecordCodecBuilder<O, O> apply(Function8<T1, T2, T3, T4, T5, T6, T7, T8, O> function) {
            return ap8(t1, t2, t3, t4, t5, t6, t7, t8, function);
        }
    }

    private static <O, T1> RecordCodecBuilder<O, O> ap1(RecordCodecBuilder<O, T1> t1, Function<T1, O> function) {
        return new RecordCodecBuilder<>(new MapEncoder<O>() {
            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, O input, RecordBuilder<T> prefix) {
                t1.encoder().encode(ops, t1.getter().apply(input), prefix);
                return prefix;
            }
        }, new MapDecoder<O>() {
            @Override
            public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
                return t1.decoder().decode(ops, input).map(function);
            }
        }, o -> o);
    }
    private static <O, T1, T2> RecordCodecBuilder<O, O> ap2(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, BiFunction<T1, T2, O> function) {
        return new RecordCodecBuilder<>(new MapEncoder<O>() {
            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, O input, RecordBuilder<T> prefix) {
                t1.encoder().encode(ops, t1.getter().apply(input), prefix);
                t2.encoder().encode(ops, t2.getter().apply(input), prefix);
                return prefix;
            }
        }, new MapDecoder<O>() {
            @Override
            public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
                return
                t1.decoder().decode(ops, input).flatMap(r ->
                        t2.decoder().decode(ops, input).map(r1 ->
                                function.apply(r, r1)
                        )
                );
            }
        }, o -> o);
    }
    private static <O, T1, T2, T3> RecordCodecBuilder<O, O> ap3(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, Function3<T1, T2, T3, O> function) {
        return new RecordCodecBuilder<>(new MapEncoder<O>() {
            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, O input, RecordBuilder<T> prefix) {
                t1.encoder().encode(ops, t1.getter().apply(input), prefix);
                t2.encoder().encode(ops, t2.getter().apply(input), prefix);
                t3.encoder().encode(ops, t3.getter().apply(input), prefix);
                return prefix;
            }
        }, new MapDecoder<O>() {
            @Override
            public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
                return
                        t1.decoder().decode(ops, input).flatMap(r1 ->
                                t2.decoder().decode(ops, input).flatMap(r2 ->
                                        t3.decoder().decode(ops, input).map(r3 -> function.apply(r1, r2, r3))
                                )
                        );
            }
        }, o -> o);
    }
    private static <O, T1, T2, T3, T4> RecordCodecBuilder<O, O> ap4(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4, Function4<T1, T2, T3, T4, O> function) {
        return new RecordCodecBuilder<>(new MapEncoder<O>() {
            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, O input, RecordBuilder<T> prefix) {
                t1.encoder().encode(ops, t1.getter().apply(input), prefix);
                t2.encoder().encode(ops, t2.getter().apply(input), prefix);
                t3.encoder().encode(ops, t3.getter().apply(input), prefix);
                t4.encoder().encode(ops, t4.getter().apply(input), prefix);
                return prefix;
            }
        }, new MapDecoder<O>() {
            @Override
            public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
                return
                        t1.decoder().decode(ops, input).flatMap(r1 ->
                                t2.decoder().decode(ops, input).flatMap(r2 ->
                                        t3.decoder().decode(ops, input).flatMap(r3 ->
                                                t4.decoder().decode(ops, input).map(r4 -> function.apply(r1, r2, r3, r4)))
                                )
                        );
            }
        }, o -> o);
    }
    private static <O, T1, T2, T3, T4, T5> RecordCodecBuilder<O, O> ap5(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4, RecordCodecBuilder<O, T5> t5, Function5<T1, T2, T3, T4, T5, O> function) {
        return new RecordCodecBuilder<>(new MapEncoder<O>() {
            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, O input, RecordBuilder<T> prefix) {
                t1.encoder().encode(ops, t1.getter().apply(input), prefix);
                t2.encoder().encode(ops, t2.getter().apply(input), prefix);
                t3.encoder().encode(ops, t3.getter().apply(input), prefix);
                t4.encoder().encode(ops, t4.getter().apply(input), prefix);
                t5.encoder().encode(ops, t5.getter().apply(input), prefix);
                return prefix;
            }
        }, new MapDecoder<O>() {
            @Override
            public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
                return
                        t1.decoder().decode(ops, input).flatMap(r1 ->
                                t2.decoder().decode(ops, input).flatMap(r2 ->
                                        t3.decoder().decode(ops, input).flatMap(r3 ->
                                                t4.decoder().decode(ops, input).flatMap(r4 ->
                                                        t5.decoder().decode(ops, input).map(r5 -> function.apply(r1, r2, r3, r4, r5))))
                                )
                        );
            }
        }, o -> o);
    }
    private static <O, T1, T2, T3, T4, T5, T6> RecordCodecBuilder<O, O> ap6(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4, RecordCodecBuilder<O, T5> t5, RecordCodecBuilder<O, T6> t6, Function6<T1, T2, T3, T4, T5, T6, O> function) {
        return new RecordCodecBuilder<>(new MapEncoder<O>() {
            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, O input, RecordBuilder<T> prefix) {
                t1.encoder().encode(ops, t1.getter().apply(input), prefix);
                t2.encoder().encode(ops, t2.getter().apply(input), prefix);
                t3.encoder().encode(ops, t3.getter().apply(input), prefix);
                t4.encoder().encode(ops, t4.getter().apply(input), prefix);
                t5.encoder().encode(ops, t5.getter().apply(input), prefix);
                t6.encoder().encode(ops, t6.getter().apply(input), prefix);
                return prefix;
            }
        }, new MapDecoder<O>() {
            @Override
            public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
                return
                        t1.decoder().decode(ops, input).flatMap(r1 ->
                                t2.decoder().decode(ops, input).flatMap(r2 ->
                                        t3.decoder().decode(ops, input).flatMap(r3 ->
                                                t4.decoder().decode(ops, input).flatMap(r4 ->
                                                        t5.decoder().decode(ops, input).flatMap(r5 ->
                                                                t6.decoder().decode(ops, input).map(r6 -> function.apply(r1, r2, r3, r4, r5, r6)))))
                                )
                        );
            }
        }, o -> o);
    }
    private static <O, T1, T2, T3, T4, T5, T6, T7> RecordCodecBuilder<O, O> ap7(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4, RecordCodecBuilder<O, T5> t5, RecordCodecBuilder<O, T6> t6, RecordCodecBuilder<O, T7> t7, Function7<T1, T2, T3, T4, T5, T6, T7, O> function) {
        return new RecordCodecBuilder<>(new MapEncoder<O>() {
            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, O input, RecordBuilder<T> prefix) {
                t1.encoder().encode(ops, t1.getter().apply(input), prefix);
                t2.encoder().encode(ops, t2.getter().apply(input), prefix);
                t3.encoder().encode(ops, t3.getter().apply(input), prefix);
                t4.encoder().encode(ops, t4.getter().apply(input), prefix);
                t5.encoder().encode(ops, t5.getter().apply(input), prefix);
                t6.encoder().encode(ops, t6.getter().apply(input), prefix);
                t7.encoder().encode(ops, t7.getter().apply(input), prefix);
                return prefix;
            }
        }, new MapDecoder<O>() {
            @Override
            public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
                return
                        t1.decoder().decode(ops, input).flatMap(r1 ->
                                t2.decoder().decode(ops, input).flatMap(r2 ->
                                        t3.decoder().decode(ops, input).flatMap(r3 ->
                                                t4.decoder().decode(ops, input).flatMap(r4 ->
                                                        t5.decoder().decode(ops, input).flatMap(r5 ->
                                                                t6.decoder().decode(ops, input).flatMap(r6 ->
                                                                        t7.decoder().decode(ops, input).map(r7 -> function.apply(r1, r2, r3, r4, r5, r6, r7))))))
                                )
                        );
            }
        }, o -> o);
    }
    private static <O, T1, T2, T3, T4, T5, T6, T7, T8> RecordCodecBuilder<O, O> ap8(RecordCodecBuilder<O, T1> t1, RecordCodecBuilder<O, T2> t2, RecordCodecBuilder<O, T3> t3, RecordCodecBuilder<O, T4> t4, RecordCodecBuilder<O, T5> t5, RecordCodecBuilder<O, T6> t6, RecordCodecBuilder<O, T7> t7, RecordCodecBuilder<O, T8> t8, Function8<T1, T2, T3, T4, T5, T6, T7, T8, O> function) {
        return new RecordCodecBuilder<>(new MapEncoder<O>() {
            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, O input, RecordBuilder<T> prefix) {
                t1.encoder().encode(ops, t1.getter().apply(input), prefix);
                t2.encoder().encode(ops, t2.getter().apply(input), prefix);
                t3.encoder().encode(ops, t3.getter().apply(input), prefix);
                t4.encoder().encode(ops, t4.getter().apply(input), prefix);
                t5.encoder().encode(ops, t5.getter().apply(input), prefix);
                t6.encoder().encode(ops, t6.getter().apply(input), prefix);
                t7.encoder().encode(ops, t7.getter().apply(input), prefix);
                return prefix;
            }
        }, new MapDecoder<O>() {
            @Override
            public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
                return
                        t1.decoder().decode(ops, input).flatMap(r1 ->
                                t2.decoder().decode(ops, input).flatMap(r2 ->
                                        t3.decoder().decode(ops, input).flatMap(r3 ->
                                                t4.decoder().decode(ops, input).flatMap(r4 ->
                                                        t5.decoder().decode(ops, input).flatMap(r5 ->
                                                                t6.decoder().decode(ops, input).flatMap(r6 ->
                                                                        t7.decoder().decode(ops, input).flatMap(r7 ->
                                                                                t8.decoder().decode(ops, input).map(r8 -> function.apply(r1, r2, r3, r4, r5, r6, r7, r8)))))))
                                )
                        );
            }
        }, o -> o);
    }
}
