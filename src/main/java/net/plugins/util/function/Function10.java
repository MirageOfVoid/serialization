package net.plugins.util.function;

public interface Function10<A, B, C, D, E, F, G, H, I, J, R> {
    R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j);

    default Function5<A, B, C, D, E, Function5<F, G, H, I, J, R>> curry() {
        return (a, b, c, d, e) -> (f, g, h, i, j) -> apply(a, b, c, d, e, f, g, h, i, j);
    }
}
