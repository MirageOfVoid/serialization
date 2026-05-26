package net.plugins.serialization.codecs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.plugins.serialization.DataResult;

import java.util.ArrayList;
import java.util.List;

public class ListCodec<E> implements Codec<List<E>> {
    private <T> DataResult<T> outOfBounds(int size) {
        return DataResult.error(() -> "Size " + size + " is out of bounds: " + max + "-" + min);
    }
    private <T> DataResult<T> processError(String process, Throwable t) {
        return DataResult.error("Exception while " + process + " list: " + t.getMessage());
    }

    private final Codec<E> codec;
    private final int max;
    private final int min;

    @Override
    public DataResult<List<E>> decode(JsonElement element) {
        try {
            JsonArray array = element.getAsJsonArray();

            int size = array.size();
            if (size > max || size < min)
                return outOfBounds(size);

            List<E> list = new ArrayList<>();
            for (JsonElement e : array) {
                list.add(codec.decode(e).getOrThrow(RuntimeException::new));
            }
            return DataResult.success(list);
        } catch (RuntimeException e) {
            return processError("decoding", e);
        }
    }

    @Override
    public DataResult<JsonElement> encode(List<E> input) {
        try {
            int size = input.size();
            if (size > max || size < min)
                return outOfBounds(size);

            JsonArray array = new JsonArray();
            for (E e : input) {
                array.add(codec.encode(e).getOrThrow(RuntimeException::new));
            }
            return DataResult.success(array);
        } catch (RuntimeException e) {
            return processError("encoding", e);
        }
    }

    ListCodec(Codec<E> elementCodec, int minSize, int maxSize) {
        this.codec = elementCodec;
        this.min = minSize;
        this.max = maxSize;
    }

    @Override
    public String toString() {
        return codec + "[list]";
    }
}
