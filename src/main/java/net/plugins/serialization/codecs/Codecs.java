package net.plugins.serialization.codecs;

import com.google.gson.JsonElement;
import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;
import net.plugins.serialization.JsonOps;
import net.plugins.serialization.Unit;

import java.util.List;
import java.util.function.Function;

public class Codecs {
    public static final Codec<JsonElement> JSON_ELEMENT = fromOps(JsonOps.INSTANCE);

    public static <T> Codec<T> fromOps(DynamicOps<T> ops) {
        return Codec.UNIT.xmap(unit -> unit.convert(ops).getValue(), t -> new Unit<>(ops, t));
    }

    private static Codec<Integer> rangedInt(int min, int max, Function<Integer, String> errorMessageFactory) {
        return Codec.INT.validate(i -> i.compareTo(min) >= 0 && i.compareTo(max) <= 0 ? DataResult.success(i) : DataResult.error(() -> errorMessageFactory.apply(i)));
    }

    public static Codec<Integer> rangedInt(int min, int max) {
        return rangedInt(min, max, i -> "Value " + i + " outside of range [" + min + ";" + max + "]");
    }

    private static Codec<Float> rangedFloat(float min, float max, Function<Float, String> errorMessageFactory) {
        return Codec.FLOAT.validate(i -> i.compareTo(min) >= 0 && i.compareTo(max) <= 0 ? DataResult.success(i) : DataResult.error(() -> errorMessageFactory.apply(i)));
    }

    public static Codec<Float> rangedFloat(float min, float max) {
        return rangedFloat(min, max, f -> "Value " + f + " outside of range [" + min + ";" + max + "]");
    }

    private static Codec<Double> rangedDouble(double min, double max, Function<Double, String> errorMessageFactory) {
        return Codec.DOUBLE.validate(i -> i.compareTo(min) >= 0 && i.compareTo(max) <= 0 ? DataResult.success(i) : DataResult.error(() -> errorMessageFactory.apply(i)));
    }

    public static Codec<Double> rangedDouble(double min, double max) {
        return rangedDouble(min, max, d -> "Value " + d + " outside of range [" + min + ";" + max + "]");
    }

    public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> original) {
        return original.validate(list -> list.isEmpty() ? DataResult.error("List must not be empty") : DataResult.success(list));
    }

    public static <T> Codec<List<T>> fixedSizeList(int minSize, int maxSize, Codec<List<T>> original) {
        return original.validate(list -> {
            int size = list.size();
            if (size < minSize || size > maxSize)
                return DataResult.error("List size must be within range [" + minSize + ";" + maxSize + "]: " + size);
            return DataResult.success(list);
        });
    }

    public static Codec<String> fixedLengthString(int minLength, int maxLength) {
        return Codec.STRING.validate(string -> {
            int len = string.length();
            if (len < minLength || len > maxLength)
                return DataResult.error("String length must be within range [" + minLength + ";" + maxLength + "]: " + len);
            return DataResult.success(string);
        });
    }
}
