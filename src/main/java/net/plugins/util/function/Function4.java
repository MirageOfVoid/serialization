package net.plugins.util.function;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Function4<A, B, C, D, R> {
    R apply(A a, B b, C c, D d);

    default BiFunction<A, B, BiFunction<C, D, R>> curry() {
        return (a, b) -> (c, d) -> apply(a, b, c, d);
    }
}
