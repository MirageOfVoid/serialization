package net.plugins.serialization.ops;

import net.plugins.serialization.DataResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    default T createFloat(Float value) {
        return createNumber(value);
    }

    default T createDouble(Double value) {
        return createNumber(value);
    }

    T createBool(Boolean value);

    T createString(String value);

    T createList(List<T> value);

    T createMap(Map<String, T> value);

    DataResult<T> mergeToList(T list, List<T> value);

    DataResult<T> mergeToMap(T map, Map<String, T> value);

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

    default DataResult<Float> getFloat(T t) {
        return getNumber(t).map(Number::floatValue);
    }

    default DataResult<Double> getDouble(T t) {
        return getNumber(t).map(Number::doubleValue);
    }

    DataResult<Boolean> getBool(T t);

    DataResult<String> getString(T t);

    DataResult<List<T>> getList(T t);

    DataResult<Map<String, T>> getMap(T t);

    default ListBuilder<T> listBuilder() {
        return new ListBuilder<>(this::createList);
    }

    default MapBuilder<T> mapBuilder() {
        return new MapBuilder<>(this::createMap);
    }

    class ListBuilder<E> {
        protected final List<E> list = new ArrayList<>();

        private final Function<List<E>, E> function;

        public void add(E e) {
            list.add(e);
        }

        public E build() {
            return function.apply(list);
        }

        ListBuilder(Function<List<E>, E> function) {
            this.function = function;
        }
    }

    class MapBuilder<E> {
        protected final Map<String, E> map = new HashMap<>();

        private final Function<Map<String, E>, E> function;

        public void add(String key, E e) {
            map.put(key, e);
        }

        public E build() {
            return function.apply(map);
        }

        public MapBuilder(Function<Map<String, E>, E> function) {
            this.function = function;
        }
    }
}
