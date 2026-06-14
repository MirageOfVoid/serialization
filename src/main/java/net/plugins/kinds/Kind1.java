package net.plugins.kinds;

public interface Kind1<F extends K1, Mu extends  Kind1.Mu> extends App<Mu, F> {
    interface Mu extends K1 {}

    static <F extends K1, Mu extends Kind1.Mu> Kind1<F, Mu> unbox(App<Mu, F> box) {
        return (Kind1<F, Mu>) box;
    }

    default <T1> Products.P1<F, T1> group(App<F, T1> t1) {
        return new Products.P1<>(t1);
    }
    default <T1, T2> Products.P2<F, T1, T2> group(App<F, T1> t1, App<F, T2> t2) {
        return new Products.P2<>(t1, t2);
    }
    default <T1, T2, T3> Products.P3<F, T1, T2, T3> group(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3) {
        return new Products.P3<>(t1, t2, t3);
    }
    default <T1, T2, T3, T4> Products.P4<F, T1, T2, T3, T4> group(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4) {
        return new Products.P4<>(t1, t2, t3, t4);
    }
    default <T1, T2, T3, T4, T5> Products.P5<F, T1, T2, T3, T4, T5> group(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4, App<F, T5> t5) {
        return new Products.P5<>(t1, t2, t3, t4, t5);
    }
    default <T1, T2, T3, T4, T5, T6> Products.P6<F, T1, T2, T3, T4, T5, T6> group(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4, App<F, T5> t5, App<F, T6> t6) {
        return new Products.P6<>(t1, t2, t3, t4, t5, t6);
    }
    default <T1, T2, T3, T4, T5, T6, T7> Products.P7<F, T1, T2, T3, T4, T5, T6, T7> group(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4, App<F, T5> t5, App<F, T6> t6, App<F, T7> t7) {
        return new Products.P7<>(t1, t2, t3, t4, t5, t6, t7);
    }
    default <T1, T2, T3, T4, T5, T6, T7, T8> Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> group(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4, App<F, T5> t5, App<F, T6> t6, App<F, T7> t7, App<F, T8> t8) {
        return new Products.P8<>(t1, t2, t3, t4, t5, t6, t7, t8);
    }
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9> group(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4, App<F, T5> t5, App<F, T6> t6, App<F, T7> t7, App<F, T8> t8, App<F, T9> t9) {
        return new Products.P9<>(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Products.P10<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> group(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4, App<F, T5> t5, App<F, T6> t6, App<F, T7> t7, App<F, T8> t8, App<F, T9> t9, App<F, T10> t10) {
        return new Products.P10<>(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);
    }
}
