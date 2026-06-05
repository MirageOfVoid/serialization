package net.plugins.util;

import net.plugins.serialization.DynamicOps;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public interface MapLike<T> {
    T get(T key);

    T get(String key);

    Set<T> keySet();

    Stream<Pair<T, T>> entries();

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

            @Override
            public Stream<Pair<T, T>> entries() {
                Stream.Builder<Pair<T, T>> builder = Stream.builder();
                map.forEach((t1, t2) -> builder.add(Pair.of(t1, t2)));
                return builder.build();
            }
        };
    }

    static <T> MapLike<T> empty(DynamicOps<T> ops) {
        return of(new HashMap<>(), ops);
    }
}
