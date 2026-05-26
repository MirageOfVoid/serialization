package net.plugins.serialization;


import net.plugins.serialization.codecs.FieldCodec;
import net.plugins.serialization.products.*;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public record CodecBuilder<O, C>(FieldCodec<C> codec, Function<O, C> getter, String field) {
    public CodecBuilder {
        assert Objects.equals(field, codec.getFieldName());
    }

    public static <O, C> CodecBuilder<O, C> of(FieldCodec<C> codec, Function<O, C> getter) {
        return new CodecBuilder<>(codec, getter, codec.getFieldName());
    }

    public static <A, O> Builder1<A, O> group(CodecBuilder<O, A> codec) {
        return new Builder1<>(codec);
    }

    public static <A, B, O> Builder2<A, B, O> group(CodecBuilder<O, A> oa, CodecBuilder<O, B> ob) {
        return new Builder2<>(oa, ob);
    }

    public static <A, B, C, O> Builder3<A, B, C, O> group(CodecBuilder<O, A> oa, CodecBuilder<O, B> ob, CodecBuilder<O, C> oc) {
        return new Builder3<>(oa, ob, oc);
    }

    public static <A, B, C, D, O> Builder4<A, B, C, D, O> group(CodecBuilder<O, A> oa, CodecBuilder<O, B> ob, CodecBuilder<O, C> oc, CodecBuilder<O, D> od) {
        return new Builder4<>(oa, ob, oc, od);
    }

    public static <A, B, C, D, E, O> Builder5<A, B, C, D, E, O> group(CodecBuilder<O, A> oa, CodecBuilder<O, B> ob, CodecBuilder<O, C> oc, CodecBuilder<O, D> od, CodecBuilder<O, E> oe) {
        return new Builder5<>(oa, ob, oc, od, oe);
    }

    public static <A, B, C, D, E, F, O> Builder6<A, B, C, D, E, F, O> group(CodecBuilder<O, A> oa, CodecBuilder<O, B> ob, CodecBuilder<O, C> oc, CodecBuilder<O, D> od, CodecBuilder<O, E> oe, CodecBuilder<O, F> of) {
        return new Builder6<>(oa, ob, oc, od, oe, of);
    }

    public static <A, B, C, D, E, F, G, O> Builder7<A, B, C, D, E, F, G, O> group(CodecBuilder<O, A> oa, CodecBuilder<O, B> ob, CodecBuilder<O, C> oc, CodecBuilder<O, D> od, CodecBuilder<O, E> oe, CodecBuilder<O, F> of, CodecBuilder<O, G> og) {
        return new Builder7<>(oa, ob, oc, od, oe, of, og);
    }
}
