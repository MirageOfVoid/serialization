package net.plugins.util.function;

public interface Function8<A, B, C, D, E, F, G, H, R> {
    R apply(A a, B b, C c, D d, E e, F f, G g, H h);

    default Function4<A, B, C, D, Function4<E, F, G, H, R>> curry() {
        return (a, b, c, d) -> (e, f, g, h) -> apply(a, b, c,d, e, f, g, h);
    }
}
