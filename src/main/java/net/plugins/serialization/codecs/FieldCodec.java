package net.plugins.serialization.codecs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.plugins.serialization.CodecBuilder;
import net.plugins.serialization.DataResult;

import java.util.List;
import java.util.function.Function;

public class FieldCodec<T> implements Codec<T> {
    protected final String field;
    protected final Codec<T> codec;

    FieldCodec(String field, Codec<T> codec) {
        this.field = field;
        this.codec = codec;
    }

    public String getFieldName() {
        return field;
    }

    public <O> CodecBuilder<O, T> forGetter(Function<O, T> getter) {
        return CodecBuilder.of(this, getter);
    }

    @Override
    public FieldCodec<T> orElse(T value) {
        return new FieldCodec<>(field, codec) {
            @Override
            public DataResult<T> decode(JsonElement element) {
                DataResult<T> result = super.decode(element);
                if (result.isError())
                    return DataResult.success(value);
                return result;
            }
        };
    }

    @Override
    public DataResult<JsonElement> encode(T input) {
        try {
            JsonObject object = new JsonObject();
            object.add(field, codec.encode(input).getOrThrow());
            return DataResult.success(object);
        } catch (RuntimeException e) {
            return DataResult.error("(%s) ".formatted(this) + e.getMessage());
        }
    }

    @Override
    public DataResult<T> decode(JsonElement element) {
        if (!element.isJsonObject())
            return DataResult.error("(%s) Not a json object".formatted(this));
        JsonObject object = element.getAsJsonObject();
        if (!object.asMap().containsKey(field)) {
            return DataResult.error("(%s) Could not find field '".formatted(this) + field + "'");
        }
        T t = codec.decode(object.get(field)).getOrThrow();
        return DataResult.success(t);
    }

    public DataResult<JsonObject> appendTo(T input, JsonObject object) {
        DataResult<JsonElement> result = codec.encode(input);
        if (result.isError())
            return DataResult.error(result.error().get().messageSupplier(), object);
        object.add(field, result.getOrThrow());
        return DataResult.success(object);
    }

    @Override
    public String toString() {
        return "FieldOf(\"" + field + "\": " + codec.toString() + ")";
    }

    public static final FieldCodec<String> STRING = Codec.STRING.fieldOf("strValue");
    public static final FieldCodec<Integer> INT = Codec.INT.fieldOf("intValue");
    public static final FieldCodec<Float> FLOAT = Codec.FLOAT.fieldOf("floatValue");
    public static final FieldCodec<Double> DOUBLE = Codec.DOUBLE.fieldOf("floatValue");
    public static final FieldCodec<Boolean> BOOL = Codec.BOOL.fieldOf("boolValue");

    public static final FieldCodec<List<String>> STR_LIST = Codec.STRING.listOf().fieldOf("strList");
    public static final FieldCodec<List<Integer>> INT_LIST = Codec.INT.listOf().fieldOf("strList");
}
