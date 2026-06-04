package net.plugins.util;

import net.plugins.serialization.DynamicOps;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface MapLike<T> {
    T get(T key);

    T get(String key);

    Set<T> keySet();

    static <T> MapLike<T> of(final Map<T, T> map, DynamicOps<T> ops) {
        return new MapLike<T>() {
            @Override
            public T get(T key) {
                return map.get(key);
            }

            @Override
            public T get(String key) {
                return get(ops.createString(key));
            }

            @Override
            public Set<T> keySet() {
                return map.keySet();
            }
        };
    }

    static <T> MapLike<T> empty(DynamicOps<T> ops) {
        return of(new HashMap<>(), ops);
    }
}
