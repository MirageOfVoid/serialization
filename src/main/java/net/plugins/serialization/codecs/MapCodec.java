package net.plugins.serialization.codecs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.plugins.serialization.DataResult;

import java.util.HashMap;
import java.util.Map;

public class MapCodec<E> implements Codec<Map<String, E>> {
    private <T> DataResult<T> processError(String process, Throwable throwable) {
        return DataResult.error("Exception while " + process + " map: " + throwable.getMessage());
    }

    private final Codec<E> codec;

    @Override
    public DataResult<JsonElement> encode(Map<String, E> input) {
        try {
            JsonObject object = new JsonObject();
            input.forEach((key, value) -> {
                object.add(key, codec.encode(value).getOrThrow());
            });
            return DataResult.success(object);
        } catch (RuntimeException e) {
            return processError("encoding", e);
        }
    }

    @Override
    public DataResult<Map<String, E>> decode(JsonElement element) {
        try {
            Map<String, E> map = new HashMap<>();
            JsonObject object = element.getAsJsonObject();
            object.keySet().forEach(key -> map.put(key, codec.decode(object.get(key)).getOrThrow()));
            return DataResult.success(map);
        } catch (RuntimeException e) {
            return processError("decoding", e);
        }
    }

    MapCodec(Codec<E> elementCodec) {
        this.codec = elementCodec;
    }

    @Override
    public String toString() {
        return codec + "[map]";
    }
}
