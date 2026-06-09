package net.plugins.serialization.codecs;

import net.plugins.serialization.building.RecordCodecBuilder;
import net.plugins.util.Pair;

public class PairCodec<F, S> {
    private final Codec<F> first;
    private final Codec<S> second;

    public Codec<Pair<F, S>> build() {
        return RecordCodecBuilder.codec(inst -> inst.group(
                first.fieldOf("first").forGetter(Pair::getFirst),
                second.fieldOf("second").forGetter(Pair::getSecond)
        ).apply(Pair::of));
    }

    public PairCodec(Codec<F> first, Codec<S> second) {
        this.first = first;
        this.second = second;
    }
}
