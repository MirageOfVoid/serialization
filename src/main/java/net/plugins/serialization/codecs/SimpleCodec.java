package net.plugins.serialization.codecs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.plugins.serialization.DataResult;

public class SimpleCodec<R> implements Codec<R> {
    private final Class<R> type;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public DataResult<JsonElement> encode(R input) {
        try {
            JsonElement element = GSON.toJsonTree(input, type);
            return DataResult.success(element);
        } catch (RuntimeException e) {
            return DataResult.error(e.getMessage());
        }
    }

    @Override
    public DataResult<R> decode(JsonElement element) {
        try {
            R r = GSON.fromJson(element, type);
            return DataResult.success(r);
        } catch (RuntimeException e) {
            return DataResult.error(e.getMessage());
        }
    }

    SimpleCodec(Class<R> type) {
        this.type = type;
    }
}
