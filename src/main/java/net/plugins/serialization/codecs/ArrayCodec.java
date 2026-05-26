package net.plugins.serialization.codecs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.plugins.serialization.DataResult;

import java.lang.reflect.Array;

public class ArrayCodec<E> implements Codec<E[]> {
    private final Codec<E> codec;
    private final Class<E> type;

    @Override
    public DataResult<JsonElement> encode(E[] input) {
        if (input.length == 0)
            return DataResult.success(new JsonArray());

        JsonArray array = new JsonArray();

        for (E e : input) {
            try {
                array.add(codec.encode(e).getOrThrow());
            } catch (RuntimeException ex) {
                return DataResult.error(ex::getMessage, array);
            }
        }

        return DataResult.success(array);
    }

    @Override
    public DataResult<E[]> decode(JsonElement element) {
        if (!element.isJsonArray())
            return DataResult.error("Not a json array");

        JsonArray array = element.getAsJsonArray();

        if (array.isEmpty()) {
            @SuppressWarnings("unchecked")
            E[] e = (E[]) Array.newInstance(type, 0);
            return DataResult.success(e);
        }

        @SuppressWarnings("unchecked")
        E[] res = (E[]) Array.newInstance(type, array.size());

        for (int i = 0; i < array.size(); i++) {
            try {
                res[i] = codec.decode(array.get(i)).getOrThrow();
            } catch (RuntimeException e) {
                return DataResult.error(e::getMessage, res);
            }
        }

        return DataResult.success(res);
    }

    ArrayCodec(Codec<E> elementCodec, Class<E> type) {
        this.codec = elementCodec;
        this.type = type;
    }
}
