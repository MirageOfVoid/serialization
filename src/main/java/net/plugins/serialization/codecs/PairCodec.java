package net.plugins.serialization.codecs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.plugins.serialization.DataResult;
import net.plugins.util.Pair;

public class PairCodec<F, S> implements Codec<Pair<F, S>> {
    protected final Codec<F> first;
    protected final Codec<S> second;

    PairCodec(Codec<F> firstCodec, Codec<S> secondCodec) {
        this.first = firstCodec;
        this.second = secondCodec;
    }

    @Override
    public DataResult<JsonElement> encode(Pair<F, S> input) {
        try {
            JsonArray array = new JsonArray();
            array.add(first.encode(input.getFirst()).getOrThrow());
            array.add(second.encode(input.getSecond()).getOrThrow());
            return DataResult.success(array);
        } catch (RuntimeException e) {
            return DataResult.error(e);
        }
    }

    @Override
    public DataResult<Pair<F, S>> decode(JsonElement element) {
        try {
            JsonArray array = element.getAsJsonArray();
            F f = first.decode(array.get(0)).getOrThrow();
            S s = second.decode(array.get(1)).getOrThrow();
            return DataResult.success(new Pair<>(f, s));
        } catch (RuntimeException e) {
            return DataResult.error(e);
        }
    }

    @Override
    public String toString() {
        return "PairCodec(" + first + ";" + second + ")";
    }
}
