package net.plugins.kinds;

import java.util.function.Function;

public interface Functor<F extends K1, Mu extends Functor.Mu> extends Kind1<F, Mu> {
    interface Mu extends Kind1.Mu {}

    static <F extends K1, Mu extends Functor.Mu> Functor<F, Mu> unbox(App<Mu, F> box) {
        return (Functor<F, Mu>) box;
    }

    <T, R> App<F, R> map(Function<T, R> function, App<F, T> app);
}
