package net.plugins.util.function;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Function3<A, B, C, R> {
    R apply(A a, B b, C c);

    default Function<A, BiFunction<B, C, R>> curry() {
        return a -> (b, c) -> apply(a, b, c);
    }
}
