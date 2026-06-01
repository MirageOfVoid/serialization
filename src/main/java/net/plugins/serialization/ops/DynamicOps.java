package net.plugins.serialization.ops;

import net.plugins.serialization.DataResult;

import java.util.List;
import java.util.Map;

public interface DynamicOps<T> {
    T createNumber(Number value);

    default T createInt(Integer value) {
        return createNumber(value);
    }

    default T createLong(Long value) {
        return createNumber(value);
    }

    default T createByte(Byte value) {
        return createNumber(value);
    }

    default T createFloat(Float value) {
        return createNumber(value);
    }

    default T createDouble(Double value) {
        return createNumber(value);
    }

    default T createBool(Boolean value) {
        return createByte((byte) (value ? 1 : 0));
    }

    T createString(String value);

    DataResult<T> mergeToList(T list, List<T> value);

    DataResult<T> mergeToMap(T map, Map<String, T> value);

    T createMap(Map<String, T> value);

    T createList(List<T> value);
}
