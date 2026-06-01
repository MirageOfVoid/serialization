package net.plugins.serialization.ops;

import net.plugins.serialization.DataResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaOps implements DynamicOps<Object> {
    public static final DynamicOps<Object> INSTANCE = new JavaOps();

    private JavaOps() {}

    @Override
    public Object createNumber(Number value) {
        return value;
    }

    @Override
    public Object createString(String value) {
        return value;
    }

    @Override
    public DataResult<Object> mergeToList(Object list, List<Object> value) {
        try {
            @SuppressWarnings("unchecked")
            List<Object> l = (List<Object>) list;
            l.addAll(value);
            return DataResult.success(l);
        } catch (ClassCastException e) {
            return DataResult.error(() -> "Not a list", new ArrayList<>(value));
        }
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, Map<String, Object> value) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) map;
            m.putAll(value);
            return DataResult.success(m);
        } catch (ClassCastException e) {
            return DataResult.error(() -> "Not a map", new HashMap<>(value));
        }
    }

    @Override
    public Object createMap(Map<String, Object> value) {
        return value;
    }

    @Override
    public Object createList(List<Object> value) {
        return value;
    }
}
