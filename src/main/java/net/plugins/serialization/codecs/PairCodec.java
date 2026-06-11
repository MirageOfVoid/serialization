package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;
import net.plugins.util.Pair;

public class PairCodec<F, S> implements Codec<Pair<F, S>> {
    private final Codec<F> first;
    private final Codec<S> second;

    public PairCodec(Codec<F> first, Codec<S> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public <T> DataResult<Pair<Pair<F, S>, T>> decode(DynamicOps<T> ops, T input) {
        return first.decode(ops, input).flatMap(p1 ->
            second.decode(ops, p1.getSecond()).map(p2 ->
                Pair.of(Pair.of(p1.getFirst(), p2.getFirst()), p2.getSecond())
            )
        );
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, Pair<F, S> input, T prefix) {
        return second.encode(ops, input.getSecond(), prefix).flatMap(f -> first.encode(ops, input.getFirst(), f));
    }
}
