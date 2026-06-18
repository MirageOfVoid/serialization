package net.plugins.serialization;

import net.plugins.util.ListBuilder;
import net.plugins.util.MapLike;
import net.plugins.util.Pair;
import net.plugins.util.RecordBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class JavaOps implements DynamicOps<Object> {
    public static final DynamicOps<Object> INSTANCE = new JavaOps();

    private JavaOps() {}

    @Override
    public Object createNumber(Number value) {
        return value;
    }

    @Override
    public Object createBool(Boolean value) {
        return value;
    }

    @Override
    public Object createString(String value) {
        return value;
    }

    @Override
    public Object createList(List<Object> value) {
        return value;
    }

    @Override
    public Object createMap(MapLike<Object> value) {
        Map<Object, Object> map = new HashMap<>();
        value.entries().forEach(pair -> map.put(pair.getFirst(), pair.getSecond()));
        return map;
    }

    @Override
    public Object createMap(Stream<Pair<Object, Object>> map) {
        Map<Object, Object> r = new HashMap<>();
        map.forEach(pair -> r.put(pair.getFirst(), pair.getSecond()));
        return r;
    }

    @Override
    public Object createList(Stream<Object> list) {
        return list.toList();
    }

    @Override
    public DataResult<Object> mergeToList(Object list, List<Object> value) {
        if (list instanceof List<?>) {
            ((List<Object>) list).addAll(value);
            return DataResult.success(list);
        }
        return DataResult.error("Not a list: " + list);
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, MapLike<Object> value) {
        if (map instanceof Map<?,?>) {
            value.entries().forEach(pair -> ((Map<Object, Object>) map).put(pair.getFirst(), pair.getSecond()));
            return DataResult.success(map);
        }
        return DataResult.error("Not a map: " + map);
    }

    @Override
    public DataResult<Number> getNumber(Object o) {
        if (o instanceof Number number)
            return DataResult.success(number);

        return DataResult.error("Not a number: " + o);
    }

    @Override
    public DataResult<Boolean> getBool(Object o) {
        if (o instanceof Boolean bool)
            return DataResult.success(bool);
        return DataResult.error("Not a boolean: " + o);
    }

    @Override
    public DataResult<String> getString(Object o) {
        if (o instanceof String str)
            return DataResult.success(str);
        return DataResult.error("Not a string: " + o);
    }

    @Override
    public DataResult<List<Object>> getList(Object o) {
        if (o instanceof List<?> list)
            return DataResult.success((List<Object>) list);
        return DataResult.error("Not a list: " + o);
    }

    @Override
    public DataResult<MapLike<Object>> getMap(Object o) {
        if (o instanceof MapLike<?>)
            return DataResult.success((MapLike<Object>) o);
        return DataResult.error("Not a map: " + o);
    }

    @Override
    public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object o) {
        return getMap(o).map(MapLike::entries);
    }

    @Override
    public DataResult<Object> getFromMap(Object map, String key) {
        return getMap(map).map(m -> m.get(key));
    }

    @Override
    public DataResult<Object> getFromList(Object list, int index) {
        return getList(list).map(l -> l.get(index));
    }

    @Override
    public Object empty() {
        return null;
    }

    @Override
    public Object emptyMap() {
        return new HashMap<Object, Object>();
    }

    @Override
    public Object emptyList() {
        return new ArrayList<Object>();
    }

    @Override
    public <U> U convert(DynamicOps<U> ops, Object o) {
        if (isMap(o))
            return convertMap(ops, o);
        if (isList(o))
            return convertList(ops, o);
        if (o instanceof Number number)
            return ops.createNumber(number);
        if (o instanceof Boolean bool)
            return ops.createBool(bool);
        if (o instanceof String string)
            return ops.createString(string);
        return ops.empty();
    }

    @Override
    public boolean isMap(Object o) {
        return o instanceof Map<?,?>;
    }

    @Override
    public boolean isList(Object o) {
        return o instanceof List<?>;
    }

    @Override
    public boolean isPrimitive(Object o) {
        return !isMap(o) && !isList(o);
    }

    @Override
    public boolean isEmpty(Object o) {
        return o == null;
    }

    @Override
    public ListBuilder<Object> listBuilder() {
        return new ObjectListBuilder(INSTANCE);
    }

    @Override
    public RecordBuilder<Object> mapBuilder() {
        return null;
    }

    public static final class ObjectListBuilder extends ListBuilder.AbstractListBuilder<Object, List<Object>> {
        public ObjectListBuilder(DynamicOps<Object> ops) {
            super(ops);
        }

        @Override
        protected List<Object> initBuilder() {
            return new ArrayList<>();
        }

        @Override
        protected List<Object> append(List<Object> builder, Object element) {
            builder.add(element);
            return builder;
        }

        @Override
        protected DataResult<Object> build(List<Object> builder, Object prefix) {
            if (prefix == null)
                return DataResult.success(builder);
            if (prefix instanceof List<?> prefixList) {
                List<Object> list = new ArrayList<>();
                list.addAll(builder);
                list.addAll(prefixList);
                return DataResult.success(list);
            }
            return DataResult.error("Not a list: " + prefix);
        }
    }

    public static final class ObjectMapBuilder extends RecordBuilder.AbstractRecordBuilder<Object, Map<Object, Object>> {
        @Override
        protected Map<Object, Object> initBuilder() {
            return new HashMap<>();
        }

        @Override
        protected Map<Object, Object> append(String key, Object value, Map<Object, Object> builder) {
            builder.put(key, value);
            return builder;
        }

        @Override
        protected DataResult<Object> build(Map<Object, Object> builder, Object prefix) {
            if (prefix == null)
                return DataResult.success(builder);
            if (prefix instanceof Map<?,?> map) {
                Map<Object, Object> result = new HashMap<>();
                result.putAll(builder);
                result.putAll(map);
                return DataResult.success(result);
            }
            return DataResult.error("Not a map:  " + prefix);
        }

        public ObjectMapBuilder(DynamicOps<Object> ops) {
            super(ops);
        }
    }
}
