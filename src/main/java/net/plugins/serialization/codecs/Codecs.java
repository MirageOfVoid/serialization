package net.plugins.serialization.codecs;

import com.google.gson.JsonElement;
import net.plugins.serialization.CodecBuilder;
import net.plugins.serialization.DataResult;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class Codecs {
    public static final Codec<Integer> NON_NEGATIVE_INT;
    public static final Codec<JsonElement> JSON_ELEMENT;
    public static final Codec<JsonElement> NONNULL_JSON_ELEMENT;
    public static final Codec<String> NON_EMPTY_STR;
    public static final Codec<Stream<Integer>> INT_STREAM;

    private static final Codec<?> ERROR;

    @SuppressWarnings("unchecked")
    public static <T> Codec<T> error() {
        return (Codec<T>) ERROR;
    }

    public static <T> Codec<Map<String, T>> fixedSizeMap(int minSize, int maxSize, Codec<T> elementCodec) {
        return new MapCodec<>(elementCodec) {
            @Override
            public DataResult<JsonElement> encode(Map<String, T> input) {
                DataResult<JsonElement> result =  super.encode(input);
                if (result.isError())
                    return result;

                int size = result.getOrThrow().getAsJsonObject().size();
                if (size > maxSize || size < minSize)
                    return DataResult.error("Size " + size + " is out of bounds: " + minSize + "-" + maxSize);

                return result;
            }

            @Override
            public DataResult<Map<String, T>> decode(JsonElement element) {
                DataResult<Map<String, T>> result =  super.decode(element);
                if (result.isError())
                    return result;

                int size = result.getOrThrow().size();
                if (size > maxSize || size < minSize)
                    return DataResult.error("Size " + size + " is out of bounds: " + minSize + "-" + maxSize);

                return result;
            }
        };
    }

    public static <O, R> CodecBuilder<O, List<R>> forArrayBased(Codec<R> elementCodec, Function<O, List<R>> getter) {
        return elementCodec.listOf().fieldOf("").forGetter(getter);
    }

    static {
        NON_NEGATIVE_INT = Codec.INT.validate(integer -> {
            if (integer < 0)
                return DataResult.error("Int " + integer + " is negative");

            return DataResult.success(integer);
        });
        JSON_ELEMENT = Codec.of(DataResult::success, DataResult::success, "JsonElement");
        NONNULL_JSON_ELEMENT = JSON_ELEMENT.validate(element -> {
            if (element.isJsonNull())
                return DataResult.error("Null value");
            return DataResult.success(element);
        });
        NON_EMPTY_STR = Codec.STRING.validate(str -> {
            if (str.isEmpty())
                return DataResult.error("Empty string");

            return DataResult.success(str);
        });
        ERROR = Codec.of(_ -> DataResult.error("Invalid codec"), _ -> DataResult.error("Invalid codec"), "Error");
        INT_STREAM = Codec.INT.streamOf();
    }
}
