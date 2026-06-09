package net.plugins.util;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;

import java.util.Collection;

public interface ListBuilder<T> {
    DynamicOps<T> ops();

    ListBuilder<T> add(T element);

    ListBuilder<T> add(DataResult<T> element);

    ListBuilder<T> addAll(Collection<T> elements);

    DataResult<T> build(T prefix);

    default DataResult<T> build() {
        return build(ops().emptyList());
    }

    abstract class AbstractListBuilder<T, B> implements ListBuilder<T> {
        protected DataResult<B> builder = DataResult.success(initBuilder());
        private final DynamicOps<T> ops;

        protected abstract B initBuilder();
        protected abstract B append(B builder, T element);
        protected abstract DataResult<T> build(B builder, T prefix);

        public AbstractListBuilder(DynamicOps<T> ops) {
            this.ops = ops;
        }

        @Override
        public DynamicOps<T> ops() {
            return ops;
        }

        @Override
        public ListBuilder<T> add(T element) {
            builder = builder.map(b -> append(b, element));
            return this;
        }

        @Override
        public ListBuilder<T> add(DataResult<T> element) {
            if (element.isSuccess())
                return add(element.getOrThrow());
            return add(ops.empty());
        }

        @Override
        public ListBuilder<T> addAll(Collection<T> elements) {
            for (T t : elements)
                add(t);
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
