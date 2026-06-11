package net.plugins.util.function;

public interface Function6<A, B, C, D, E, F, R> {
    R apply(A a, B b, C c, D d, E e, F f);

    default Function3<A, B, C, Function3<D, E, F, R>> curry() {
        return (a, b, c) -> (d, e, f) -> apply(a, b, c, d, e, f);
    }
}
