package net.plugins.serialization.codecs;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.plugins.serialization.DataResult;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class EnumCodec<E extends Enum<E>> implements Codec<E> {
    private final Map<String, E> map;

    @Override
    public DataResult<JsonElement> encode(E input) {
        try {
            String name = input.name();
            return DataResult.success(new JsonPrimitive(name));
        } catch (RuntimeException e) {
            return DataResult.error(e);
        }
    }

    @Override
    public DataResult<E> decode(JsonElement element) {
        try {
            String name = element.getAsJsonPrimitive().getAsString();
            if (!map.containsKey(name)) {
                throw new NoSuchElementException("could not field enum element named '" + name + "'");
            }
            return DataResult.success(map.get(name));
        } catch (RuntimeException e) {
            return DataResult.error(e);
        }
    }

    EnumCodec(E[] values) {
        this.map = Arrays.stream(values).collect(Collectors.toMap(Enum::name, e -> e));
    }
}
