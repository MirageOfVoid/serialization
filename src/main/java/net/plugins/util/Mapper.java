package net.plugins.util;

import net.plugins.serialization.DataResult;

import java.util.function.Function;

public record Mapper<T, T1>(Function<T, DataResult<T1>> to, Function<T1, DataResult<T>> from) {
    public DataResult<T1> applyTo(T t) {
        return to.apply(t);
    }

    public DataResult<T> applyFrom(T1 t1) {
        return from.apply(t1);
    }
}
