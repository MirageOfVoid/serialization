package net.plugins.util;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;

public interface ListBuilder<T> {
    DynamicOps<T> ops();

    ListBuilder<T> add(T element);

    ListBuilder<T> add(DataResult<T> element);

    DataResult<T> build(T prefix);

    default DataResult<T> build() {
        return build(ops().emptyList());
    }
}
