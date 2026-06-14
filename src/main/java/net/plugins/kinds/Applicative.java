package net.plugins.kinds;

import net.plugins.util.function.*;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Applicative<F extends K1, Mu extends Applicative.Mu> extends Functor<F, Mu> {
    interface Mu extends Functor.Mu {}

    static <F extends K1, Mu extends Applicative.Mu> Applicative<F, Mu> unbox(App<Mu, F> box) {
        return (Applicative<F, Mu>) box;
    }

    <A> App<F, A> point(A a);

    <A, R> Function<App<F, A>, App<F, R>> lift(App<F, Function<A, R>> function);

    default <A, R> App<F, R> ap(App<F, Function<A, R>> function, App<F, A> arg) {
        return lift(function).apply(arg);
    }

    default <A, R> App<F, R> ap(Function<A, R> function, App<F, A> arg) {
        return map(function, arg);
    }

    default <A, B, R> App<F, R> ap2(App<F, BiFunction<A, B, R>> function, App<F, A> a, App<F, B> b) {
        Function<BiFunction<A, B, R>, Function<A, Function<B, R>>> curry = f -> a1 -> b1 -> f.apply(a1, b1);
        return ap(ap(map(curry, function), a), b);
    }

    default <A, B, C, R> App<F, R> ap3(App<F, Function3<A, B, C, R>> func, App<F, A> a, App<F, B> b, App<F, C> c) {
        return ap2(ap(map(Function3::curry, func), a), b, c);
    }

    default <A, B, C, D, R> App<F, R> ap4(App<F, Function4<A, B, C, D, R>> func, App<F, A> a, App<F, B> b, App<F, C> c, App<F, D> d) {
        return ap2(ap2(map(Function4::curry, func), a, b), c, d);
    }

    default <A, B, C, D, E, R> App<F, R> ap5(App<F, Function5<A, B, C, D, E, R>> func, App<F, A> a, App<F, B> b, App<F, C> c, App<F, D> d, App<F, E> e) {
        return ap3(ap2(map(Function5::curry, func), a, b), c, d, e);
    }

    default <A, B, C, D, E, F1, R> App<F, R> ap6(App<F, Function6<A, B, C, D, E, F1, R>> func, App<F, A> a, App<F, B> b, App<F, C> c, App<F, D> d, App<F, E> e, App<F, F1> f) {
        return ap3(ap3(map(Function6::curry, func), a, b, c), d, e, f);
    }

    default <A, B, C, D, E, F1, G, R> App<F, R> ap7(App<F, Function7<A, B, C, D, E, F1, G, R>> func, App<F, A> a, App<F, B> b, App<F, C> c, App<F, D> d, App<F, E> e, App<F, F1> f, App<F, G> g) {
        return ap4(ap3(map(Function7::curry, func), a, b, c), d, e, f, g);
    }

    default <A, B, C, D, E, F1, G, H, R> App<F, R> ap8(App<F, Function8<A, B, C, D, E, F1, G, H, R>> func, App<F, A> a, App<F, B> b, App<F, C> c, App<F, D> d, App<F, E> e, App<F, F1> f, App<F, G> g, App<F, H> h) {
        return ap4(ap4(map(Function8::curry, func), a, b, c, d), e, f, g, h);
    }

    default <A, B, C, D, E, F1, G, H, I, R> App<F, R> ap9(App<F, Function9<A, B, C, D, E, F1, G, H, I, R>> func, App<F, A> a, App<F, B> b, App<F, C> c, App<F, D> d, App<F, E> e, App<F, F1> f, App<F, G> g, App<F, H> h, App<F, I> i) {
        return ap5(ap4(map(Function9::curry, func), a, b, c, d), e, f, g, h, i);
    }

    default <A, B, C, D, E, F1, G, H, I, J, R> App<F, R> ap10(App<F, Function10<A, B, C, D, E, F1, G, H, I, J, R>> func, App<F, A> a, App<F, B> b, App<F, C> c, App<F, D> d, App<F, E> e, App<F, F1> f, App<F, G> g, App<F, H> h, App<F, I> i, App<F, J> j) {
        return ap5(ap5(map(Function10::curry, func), a, b, c, d, e), f, g, h, i, j);
    }

    default <A, B, R> App<F, R> apply2(BiFunction<A, B, R> function, App<F, A> a, App<F, B> b) {
        return ap2(point(function), a, b);
    }

    default <A, B, C, R> App<F, R> apply3(Function3<A, B, C, R> function, App<F, A> a, App<F, B> b, App<F, C> c) {
        return ap3(point(function), a, b, c);
    }

    default <A, B, C, D, R> App<F, R> apply4(Function4<A, B, C, D, R> function, App<F, A> a, App<F, B> b, App<F, C> c, App<F, D> d) {
        return ap4(point(function), a, b, c, d);
    }
}
