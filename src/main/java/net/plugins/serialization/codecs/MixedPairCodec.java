package net.plugins.serialization.codecs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.plugins.serialization.DataResult;
import net.plugins.util.Pair;

public class MixedPairCodec<F, S> extends PairCodec<F, S> {
    MixedPairCodec(FieldCodec<F> firstCodec, FieldCodec<S> secondCodec) {
        super(firstCodec, secondCodec);
    }

    @Override
    public DataResult<JsonElement> encode(Pair<F, S> input) {
        try {
            JsonObject f = first.encode(input.getFirst()).getOrThrow().getAsJsonObject();
            JsonObject s = second.encode(input.getSecond()).getOrThrow().getAsJsonObject();
            return DataResult.success(mix(f, s));
        } catch (RuntimeException e) {
            return DataResult.error(e);
        }
    }

    @Override
    public DataResult<Pair<F, S>> decode(JsonElement element) {
        try {
            JsonObject object = element.getAsJsonObject();
            F f = first.decode(object).getOrThrow();
            S s = second.decode(object).getOrThrow();
            return DataResult.success(new Pair<>(f, s));
        } catch (RuntimeException e) {
            return DataResult.error(e);
        }
    }

    @Override
    public String toString() {
        return "Mixed" + super.toString();
    }

    private static JsonObject mix(JsonObject f, JsonObject s) {
        JsonObject object = new JsonObject();

        for (String key : s.keySet()) {
            object.add(key, s.get(key));
        }
        for (String key : f.keySet()) {
            object.add(key, f.get(key));
        }

        return object;
    }
}
