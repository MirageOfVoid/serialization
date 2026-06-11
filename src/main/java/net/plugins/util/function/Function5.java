package net.plugins.util.function;

import java.util.function.BiFunction;

public interface Function5<A, B, C, D, E, R> {
    R apply(A a, B b, C c, D d, E e);

    default BiFunction<A, B, Function3<C, D, E, R>> curry() {
        return (a, b) -> (c, d, e) -> apply(a, b, c, d, e);
    }
}
