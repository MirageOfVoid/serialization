package net.plugins.util.function;

public interface Function9<A, B, C, D, E, F, G, H, I, R> {
    R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i);

    default Function4<A, B, C, D, Function5<E, F, G, H, I, R>> curry() {
        return (a, b, c, d) -> (e, f, g, h, i) -> apply(a, b, c, d, e, f, g, h, i);
    }
}
