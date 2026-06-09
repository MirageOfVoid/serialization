package net.plugins.serialization;

import net.plugins.util.ListBuilder;
import net.plugins.util.MapLike;
import net.plugins.util.RecordBuilder;

import java.util.*;
import java.util.function.Function;

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

    default T createShort(Short value) {
        return createNumber(value);
    }

    default T createFloat(Float value) {
        return createNumber(value);
    }

    default T createDouble(Double value) {
        return createNumber(value);
    }

    T createBool(Boolean value);

    T createString(String value);

    T createList(List<T> value);

    T createMap(MapLike<T> value);

    DataResult<T> mergeToList(T list, List<T> value);

    DataResult<T> mergeToMap(T map, MapLike<T> value);

    DataResult<Number> getNumber(T t);

    default DataResult<Integer> getInt(T t) {
        return getNumber(t).map(Number::intValue);
    }

    default DataResult<Long> getLong(T t) {
        return getNumber(t).map(Number::longValue);
    }

    default DataResult<Byte> getByte(T t) {
        return getNumber(t).map(Number::byteValue);
    }

    default DataResult<Short> getShort(T t) {
        return getNumber(t).map(Number::shortValue);
    }

    default DataResult<Float> getFloat(T t) {
        return getNumber(t).map(Number::floatValue);
    }

    default DataResult<Double> getDouble(T t) {
        return getNumber(t).map(Number::doubleValue);
    }

    DataResult<Boolean> getBool(T t);

    DataResult<String> getString(T t);

    DataResult<List<T>> getList(T t);

    DataResult<MapLike<T>> getMap(T t);

    DataResult<T> getFromMap(T map, String key);

    DataResult<T> getFromList(T list, int index);

    default DataResult<T> mergeToPrimitive(T prefix, T value) {
        return DataResult.success(value);
    }

    T empty();

    T emptyMap();

    T emptyList();

    T clone(T t);

    boolean isMap(T t);

    boolean isList(T t);

    boolean isPrimitive(T t);

    boolean isEmpty(T t);

    ListBuilder<T> listBuilder();

    RecordBuilder<T> mapBuilder();
}
