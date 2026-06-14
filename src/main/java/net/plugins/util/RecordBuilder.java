package net.plugins.util;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;

public interface RecordBuilder<T> {
    DynamicOps<T> ops();

    RecordBuilder<T> add(T key, T value);

    RecordBuilder<T> add(T key, DataResult<T> value);

    RecordBuilder<T> add(DataResult<T> key, DataResult<T> value);

    RecordBuilder<T> withErrorsFrom(DataResult<?> result);

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

    abstract class AbstractRecordBuilder<T, B> implements RecordBuilder<T> {
        protected DataResult<B> builder = DataResult.success(initBuilder());
        private final DynamicOps<T> ops;

        protected abstract B initBuilder();
        protected abstract B append(String key, T value, B builder);
        protected abstract DataResult<T> build(B builder, T prefix);

        public AbstractRecordBuilder(DynamicOps<T> ops) {
            this.ops = ops;
        }

        @Override
        public DynamicOps<T> ops() {
            return ops;
        }

        @Override
        public RecordBuilder<T> add(T key, T value) {
            builder = ops.getString(key).flatMap(k -> {
                add(k, value);
                return builder;
            });
            return this;
        }

        @Override
        public RecordBuilder<T> add(String key, T value) {
            builder = builder.map(b -> append(key, value, b));
            return this;
        }

        @Override
        public RecordBuilder<T> add(T key, DataResult<T> value) {
            builder = ops.getString(key).flatMap(k -> {
                add(k, value);
                return builder;
            });
            return this;
        }

        @Override
        public RecordBuilder<T> add(String key, DataResult<T> value) {
            builder = builder.apply2((b, v) -> append(key, v, b), value);
            return this;
        }

        @Override
        public RecordBuilder<T> add(DataResult<T> key, DataResult<T> value) {
            builder = key.flatMap(t -> {
                add(t, value);
                return builder;
            });
            return this;
        }

        @Override
        public RecordBuilder<T> withErrorsFrom(DataResult<?> result) {
            builder = builder.flatMap(b -> result.map(r -> b));
            return this;
        }

        @Override
        public DataResult<T> build(T prefix) {
            DataResult<T> result = builder.flatMap(b -> build(b, prefix));
            builder = DataResult.success(initBuilder());
            return result;
        }
    }
}
