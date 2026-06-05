package net.plugins.serialization.building;

import net.plugins.Main;
import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;
import net.plugins.serialization.MapDecoder;
import net.plugins.serialization.MapEncoder;
import net.plugins.serialization.codecs.MapCodec;
import net.plugins.util.MapLike;
import net.plugins.util.Pair;
import net.plugins.util.RecordBuilder;
import net.plugins.util.function.Function3;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Products {
    record P1<O, T1>(CodecBuilder<O, T1>  t1) {
        public MapCodec<O> apply(Function<T1, O> function) {
            return null;
        }
    }
    record P2<O, T1, T2>(CodecBuilder<O, T1>  t1, CodecBuilder<O, T2> t2) {
        public CodecBuilder<O, O> apply(BiFunction<T1, T2, O> function) {
            return ap2(t1, t2, function);
        }
    }
    record P3<O, T1, T2, T3>(CodecBuilder<O, T1>  t1, CodecBuilder<O, T2> t2, CodecBuilder<O, T3> t3) {
        public CodecBuilder<O, O> apply(Function3<T1, T2, T3, O> function) {
            return ap3(t1, t2, t3, function);
        }
    }
    record P4<O, T1, T2, T3, T4>(CodecBuilder<O, T1>  t1, CodecBuilder<O, T2> t2, CodecBuilder<O, T3> t3, CodecBuilder<O, T4> t4) {
        public MapCodec<O> apply(BiFunction<T1, T2, O> function) {
            return null;
        }
    }

    private static <O, T1, T2> CodecBuilder<O, O> ap2(CodecBuilder<O, T1> t1, CodecBuilder<O, T2> t2, BiFunction<T1, T2, O> function) {
        return new CodecBuilder<>(new MapEncoder<O>() {
            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, O input, RecordBuilder<T> prefix) {
                t1.encoder().encode(ops, t1.getter().apply(input), prefix);
                t2.encoder().encode(ops, t2.getter().apply(input), prefix);
                return prefix;
            }

            @Override
            public <T> DataResult<T> compressedEncode(DynamicOps<T> ops, O input, T prefix) {
                t1.encoder().compressedEncode(ops, t1.getter().apply(input), prefix);
                t2.encoder().compressedEncode(ops, t2.getter().apply(input), prefix);
                return DataResult.success(prefix);
            }
        }, new MapDecoder<O>() {
            @Override
            public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
                return t1.decoder().decode(ops, input).flatMap(r ->
                        t2.decoder().decode(ops, input).map(r1 ->
                                function.apply(r, r1)
                        )
                );
            }

            @Override
            public <T> DataResult<Pair<O, T>> compressedDecode(DynamicOps<T> ops, T t) {
                DataResult<O> result = t1.decoder().compressedDecode(ops, t).flatMap(r ->
                        t2.decoder().compressedDecode(ops, t).map(r1 ->
                                function.apply(r.getFirst(), r1.getFirst())
                        )
                );

                if (result.isError()) {
                    return result.error().get().cast();
                }

                return DataResult.success(Pair.of(result.getOrThrow(), t));
            }
        }, o -> o);
    }

    private static <O, T1, T2, T3> CodecBuilder<O, O> ap3(CodecBuilder<O, T1> t1, CodecBuilder<O, T2> t2, CodecBuilder<O, T3> t3, Function3<T1, T2, T3, O> function) {
        return new CodecBuilder<>(new MapEncoder<O>() {
            @Override
            public <T> RecordBuilder<T> encode(DynamicOps<T> ops, O input, RecordBuilder<T> prefix) {
                t1.encoder().encode(ops, t1.getter().apply(input), prefix);
                t2.encoder().encode(ops, t2.getter().apply(input), prefix);
                t3.encoder().encode(ops, t3.getter().apply(input), prefix);
                return prefix;
            }

            @Override
            public <T> DataResult<T> compressedEncode(DynamicOps<T> ops, O input, T prefix) {
                t1.encoder().compressedEncode(ops, t1.getter().apply(input), prefix);
                t2.encoder().compressedEncode(ops, t2.getter().apply(input), prefix);
                t3.encoder().compressedEncode(ops, t3.getter().apply(input), prefix);
                return DataResult.success(prefix);
            }
        }, new MapDecoder<O>() {
            @Override
            public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
                return t1.decoder().decode(ops, input).flatMap(r ->
                        t2.decoder().decode(ops, input).flatMap(r1 ->
                                t3.decoder().decode(ops, input).map(r2 ->
                                        function.apply(r, r1, r2)
                                )
                        )
                );
            }

            @Override
            public <T> DataResult<Pair<O, T>> compressedDecode(DynamicOps<T> ops, T t) {
                DataResult<O> result = t1.decoder().compressedDecode(ops, t).flatMap(r ->
                        t2.decoder().compressedDecode(ops, t).flatMap(r1 ->
                                t3.decoder().compressedDecode(ops, t).map(r2 ->
                                        function.apply(r.getFirst(), r1.getFirst(), r2.getFirst())
                                )
                        )
                );

                if (result.isError()) {
                    return result.error().get().cast();
                }

                return DataResult.success(Pair.of(result.getOrThrow(), t));
            }
        }, o -> o);
    }
}
