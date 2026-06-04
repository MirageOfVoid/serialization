package net.plugins.util;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;

public interface RecordBuilder<T> {
    DynamicOps<T> ops();

    RecordBuilder<T> add(T key, T value);

    RecordBuilder<T> add(T key, DataResult<T> value);

    RecordBuilder<T> add(DataResult<T> key, DataResult<T> value);

    DataResult<T> build(T prefix);

    default RecordBuilder<T> add(String key, T value) {
        return add(ops().createString(key), value);
    }

    default RecordBuilder<T> add(String key, DataResult<T> value) {
        return add(ops().createString(key), value);
    }

    default DataResult<T> build() {
        return build(ops().emptyMap());
    }
}
