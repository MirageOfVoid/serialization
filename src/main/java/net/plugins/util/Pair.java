package net.plugins.util;

import java.util.function.Function;

public class Pair<F,  S> {
    private final F first;
    private final S second;

    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }

    private Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public S getSecond() {
        return second;
    }

    public F getFirst() {
        return first;
    }

    public Pair<S, F> swap() {
        return of(second, first);
    }

    public <F2> Pair<F2, S> mapFirst(Function<F, F2> mapper) {
        return of(mapper.apply(first), second);
    }

    public <S2> Pair<F, S2> mapSecond(Function<S, S2> mapper) {
        return of(first, mapper.apply(second));
    }

    public <F2, S2> Pair<F2, S2> map(Function<F, F2> first, Function<S, S2> second) {
        return mapFirst(first).mapSecond(second);
    }

    @Override
    public String toString() {
        return first + ":" + second;
    }
}
