package net.plugins.serialization.codecs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.plugins.serialization.DataResult;

import java.util.HashMap;
import java.util.Map;

public class MapCodec<E> implements Codec<Map<String, E>> {
    private final Codec<E> codec;

    @Override
    public DataResult<JsonElement> encode(Map<String, E> input) {
        if (input.isEmpty())
            return DataResult.success(new JsonObject());

        JsonObject object = new JsonObject();
        for (String key : input.keySet()) {
            try {
                object.add(key, codec.encode(input.get(key)).getOrThrow());
            } catch (RuntimeException e) {
                return DataResult.error(() -> "(%s) ".formatted(this) + e.getMessage(), object);
            }
        }
        return DataResult.success(object);
    }

    @Override
    public DataResult<Map<String, E>> decode(JsonElement element) {
        if (!element.isJsonObject())
            return DataResult.error("(%s) Not a json object".formatted(this));
        JsonObject object = element.getAsJsonObject();
        if (object.isEmpty())
            return DataResult.success(new HashMap<>());
        Map<String, E> map = new HashMap<>();
        for (String key : object.keySet()) {
            try {
                map.put(key, codec.decode(object.get(key)).getOrThrow());
            } catch (RuntimeException e) {
                return DataResult.error(() -> "(%s) ".formatted(this) + e.getMessage(), map);
            }
        }
        return DataResult.success(map);
    }

    MapCodec(Codec<E> elementCodec) {
        this.codec = elementCodec;
    }

    @Override
    public String toString() {
        return codec + "[map]";
    }
}
