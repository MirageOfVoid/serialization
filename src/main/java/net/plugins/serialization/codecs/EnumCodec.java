package net.plugins.serialization.codecs;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.plugins.serialization.DataResult;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class EnumCodec<E extends Enum<E>> implements Codec<E> {
    private final Map<String, E> map;

    @Override
    public DataResult<JsonElement> encode(E input) {
        return DataResult.success(new JsonPrimitive(input.name()));
    }

    @Override
    public DataResult<E> decode(JsonElement element) {
        if (!element.isJsonPrimitive())
            return DataResult.error("(%s) Not a json primitive".formatted(this));
        String name = element.getAsJsonPrimitive().getAsString();
        if (!map.containsKey(name))
            return DataResult.error("(%s) Could not find enum element '".formatted(this) + name + "'");
        return DataResult.success(map.get(name));
    }

    EnumCodec(E[] values) {
        this.map = Arrays.stream(values).collect(Collectors.toMap(Enum::name, e -> e));
    }
}
