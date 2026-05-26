package net.plugins.serialization.codecs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.plugins.serialization.DataResult;

import java.util.ArrayList;
import java.util.List;

public class ListCodec<E> implements Codec<List<E>> {
    private final Codec<E> codec;

    @Override
    public DataResult<List<E>> decode(JsonElement element) {
        if (!element.isJsonArray())
            return DataResult.error("Not a json array");
        JsonArray array = element.getAsJsonArray();
        if (array.isEmpty())
            return DataResult.success(new ArrayList<>());
        List<E> list = new ArrayList<>();
        for (JsonElement e : array) {
            try {
                list.add(codec.decode(e).getOrThrow(RuntimeException::new));
            } catch (RuntimeException ex) {
                return DataResult.error(ex::getMessage, list);
            }
        }
        return DataResult.success(list);
    }

    @Override
    public DataResult<JsonElement> encode(List<E> input) {
        if (input.isEmpty())
            return DataResult.success(new JsonArray());
        JsonArray array = new JsonArray();
        for (E e : input) {
            try {
                array.add(codec.encode(e).getOrThrow(RuntimeException::new));
            } catch (RuntimeException ex) {
                return DataResult.error(ex::getMessage, array);
            }
        }
        return DataResult.success(array);
    }

    ListCodec(Codec<E> elementCodec) {
        this.codec = elementCodec;
    }

    @Override
    public String toString() {
        return codec + "[list]";
    }
}
