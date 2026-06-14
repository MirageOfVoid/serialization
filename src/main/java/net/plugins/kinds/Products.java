package net.plugins.kinds;

import net.plugins.util.function.*;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Products {
    record P1<F extends K1, T1>(App<F, T1> t1) {
        public <R> App<F, R> apply(Applicative<F, ?> instance, Function<T1, R> function) {
            return apply(instance, instance.point(function));
        }
        public <R> App<F, R> apply(Applicative<F, ?> instance, App<F, Function<T1, R>> function) {
            return instance.ap(function, t1);
        }
    }
    record P2<F extends K1, T1, T2>(App<F, T1> t1, App<F, T2> t2) {
        public <R> App<F, R> apply(Applicative<F, ?> instance, BiFunction<T1, T2, R> function) {
            return apply(instance, instance.point(function));
        }
        public <R> App<F, R> apply(Applicative<F, ?> instance, App<F, BiFunction<T1, T2, R>> function) {
            return instance.ap2(function, t1, t2);
        }
    }
    record P3<F extends K1, T1, T2, T3>(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3) {
        public <R> App<F, R> apply(Applicative<F, ?> instance, Function3<T1, T2, T3, R> function) {
            return apply(instance, instance.point(function));
        }
        public <R> App<F, R> apply(Applicative<F, ?> instance, App<F, Function3<T1, T2, T3, R>> function) {
            return instance.ap3(function, t1, t2, t3);
        }
    }
    record P4<F extends K1, T1, T2, T3, T4>(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4) {
        public <R> App<F, R> apply(Applicative<F, ?> instance, Function4<T1, T2, T3, T4, R> function) {
            return apply(instance, instance.point(function));
        }
        public <R> App<F, R> apply(Applicative<F, ?> instance, App<F, Function4<T1, T2, T3, T4, R>> function) {
            return instance.ap4(function, t1, t2, t3, t4);
        }
    }
    record P5<F extends K1, T1, T2, T3, T4, T5>(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4, App<F, T5> t5) {
        public <R> App<F, R> apply(Applicative<F, ?> instance, Function5<T1, T2, T3, T4, T5, R> function) {
            return apply(instance, instance.point(function));
        }
        public <R> App<F, R> apply(Applicative<F, ?> instance, App<F, Function5<T1, T2, T3, T4, T5, R>> function) {
            return instance.ap5(function, t1, t2, t3, t4, t5);
        }
    }
    record P6<F extends K1, T1, T2, T3, T4, T5, T6>(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4, App<F, T5> t5, App<F, T6> t6) {
        public <R> App<F, R> apply(Applicative<F, ?> instance, Function6<T1, T2, T3, T4, T5, T6, R> function) {
            return apply(instance, instance.point(function));
        }
        public <R> App<F, R> apply(Applicative<F, ?> instance, App<F, Function6<T1, T2, T3, T4, T5, T6, R>> function) {
            return instance.ap6(function, t1, t2, t3, t4, t5, t6);
        }
    }
    record P7<F extends K1, T1, T2, T3, T4, T5, T6, T7>(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4, App<F, T5> t5, App<F, T6> t6, App<F, T7> t7) {
        public <R> App<F, R> apply(Applicative<F, ?> instance, Function7<T1, T2, T3, T4, T5, T6, T7, R> function) {
            return apply(instance, instance.point(function));
        }
        public <R> App<F, R> apply(Applicative<F, ?> instance, App<F, Function7<T1, T2, T3, T4, T5, T6, T7, R>> function) {
            return instance.ap7(function, t1, t2, t3, t4, t5, t6, t7);
        }
    }
    record P8<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8>(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4, App<F, T5> t5, App<F, T6> t6, App<F, T7> t7, App<F, T8> t8) {
        public <R> App<F, R> apply(Applicative<F, ?> instance, Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> function) {
            return apply(instance, instance.point(function));
        }
        public <R> App<F, R> apply(Applicative<F, ?> instance, App<F, Function8<T1, T2, T3, T4, T5, T6, T7, T8, R>> function) {
            return instance.ap8(function, t1, t2, t3, t4, t5, t6, t7, t8);
        }
    }
    record P9<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9>(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4, App<F, T5> t5, App<F, T6> t6, App<F, T7> t7, App<F, T8> t8, App<F, T9> t9) {
        public <R> App<F, R> apply(Applicative<F, ?> instance, Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> function) {
            return apply(instance, instance.point(function));
        }
        public <R> App<F, R> apply(Applicative<F, ?> instance, App<F, Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>> function) {
            return instance.ap9(function, t1, t2, t3, t4, t5, t6, t7, t8, t9);
        }
    }
    record P10<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(App<F, T1> t1, App<F, T2> t2, App<F, T3> t3, App<F, T4> t4, App<F, T5> t5, App<F, T6> t6, App<F, T7> t7, App<F, T8> t8, App<F, T9> t9, App<F, T10> t10) {
        public <R> App<F, R> apply(Applicative<F, ?> instance, Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> function) {
            return apply(instance, instance.point(function));
        }
        public <R> App<F, R> apply(Applicative<F, ?> instance, App<F, Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>> function) {
            return instance.ap10(function, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);
        }
    }
}
