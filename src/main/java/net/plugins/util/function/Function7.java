package net.plugins.util.function;

public interface Function7<A, B, C, D, E, F, G, R> {
    R apply(A a, B b, C c, D d, E e, F f, G g);

    default Function3<A, B, C, Function4<D, E, F, G, R>> curry() {
        return (a, b, c) -> (d, e, f, g) -> apply(a, b, c, d, e, f, g);
    }
}
