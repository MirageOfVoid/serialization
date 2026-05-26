package net.plugins.serialization;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface DataResult<R> permits DataResult.Success, DataResult.Error {
    static <R> DataResult<R> success(R value) {
        return new Success<>(Objects.requireNonNull(value));
    }

    static <R> DataResult<R> error(Supplier<String> messageSupplier, R partialValue) {
        return new Error<>(messageSupplier, Optional.of(partialValue));
    }

    static <R> DataResult<R> error(Supplier<String> messageSupplier) {
        return new Error<>(messageSupplier, Optional.empty());
    }

    static <R> DataResult<R> error(String message) {
        return error(() -> message);
    }

    static <R> DataResult<R> error(Throwable throwable) {
        return error(throwable.getMessage());
    }

    Optional<R> result();

    Optional<Error<R>> error();

    boolean isError();

    boolean isSuccess();

    Optional<R> resultOrPartial();

    Optional<R> resultOrPartial(Consumer<String> onError);

    <E extends Throwable> R getOrThrow(Function<String, E> e) throws E;

    R getOrThrow();

    R getOrElse(Supplier<R> value);

    DataResult<R> orElse(Supplier<R> value);

    void ifSuccess(Consumer<R> ifSuccess);

    void ifError(Consumer<Error<R>> ifError);

    DataResult<R> setResult(R value);

    DataResult<R> setPartial(R partial);

    <T> DataResult<T> map(Function<R, T> mapper);

    <T> DataResult<T> flatMap(Function<R, ? extends DataResult<T>> mapper);

    record Success<R>(R value) implements DataResult<R> {

        @Override
        public Optional<R> result() {
            return Optional.of(value);
        }

        @Override
        public Optional<Error<R>> error() {
            return Optional.empty();
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public Optional<R> resultOrPartial() {
            return Optional.of(value);
        }

        @Override
        public Optional<R> resultOrPartial(Consumer<String> onError) {
            return Optional.of(value);
        }

        @Override
        public void ifSuccess(Consumer<R> ifSuccess) {
            ifSuccess.accept(value);
        }

        @Override
        public void ifError(Consumer<Error<R>> ifError) {}

        @Override
        public DataResult<R> setResult(R value) {
            return new Success<>(value);
        }

        @Override
        public DataResult<R> setPartial(R partial) {
            return this;
        }

        @Override
        public <T> DataResult<T> map(Function<R, T> mapper) {
            return new Success<>(mapper.apply(value));
        }

        @Override
        public <T> DataResult<T> flatMap(Function<R, ? extends DataResult<T>> mapper) {
            return mapper.apply(value);
        }

        @Override
        public <E extends Throwable> R getOrThrow(Function<String, E> e) throws E {
            return value;
        }

        @Override
        public R getOrThrow() {
            return value;
        }

        @Override
        public R getOrElse(Supplier<R> value) {
            return this.value;
        }

        @Override
        public DataResult<R> orElse(Supplier<R> value) {
            return this;
        }

        @Override
        public @NotNull String toString() {
            return "{DataResult.Success}";
        }
    }

    record Error<R>(Supplier<String> messageSupplier, Optional<R> partialValue) implements DataResult<R> {
        public String getMessage() {
            return messageSupplier.get();
        }

        @Override
        public Optional<R> result() {
            return Optional.empty();
        }

        @Override
        public Optional<Error<R>> error() {
            return Optional.of(this);
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public Optional<R> resultOrPartial() {
            return partialValue;
        }

        @Override
        public Optional<R> resultOrPartial(Consumer<String> onError) {
            onError.accept(getMessage());
            return partialValue;
        }

        @Override
        public void ifSuccess(Consumer<R> ifSuccess) {}

        @Override
        public void ifError(Consumer<Error<R>> ifError) {
            ifError.accept(this);
        }

        @Override
        public DataResult<R> setResult(R value) {
            return this;
        }

        @Override
        public DataResult<R> setPartial(R partial) {
            return new Error<>(messageSupplier, Optional.of(partial));
        }

        @Override
        public <T> DataResult<T> map(Function<R, T> mapper) {
            if (partialValue.isEmpty())
                return new Error<>(messageSupplier, Optional.empty());

            return new Error<>(messageSupplier, partialValue.map(mapper));
        }

        @Override
        public <T> DataResult<T> flatMap(Function<R, ? extends DataResult<T>> mapper) {
            if (partialValue.isEmpty())
                return new Error<>(messageSupplier, Optional.empty());

            DataResult<T> result = mapper.apply(partialValue.get());
            if (result instanceof Success<T> success) {
                return new Error<>(messageSupplier, Optional.of((success.value())));
            } else if (result instanceof DataResult.Error<T> error) {
                return new Error<>(() -> mergeMessages(getMessage(), error.getMessage()), error.partialValue);
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public <E extends Throwable> R getOrThrow(Function<String, E> e) throws E {
            if (partialValue.isEmpty())
                throw e.apply(getMessage());

            return partialValue.get();
        }

        @Override
        public R getOrThrow() {
            return getOrThrow(RuntimeException::new);
        }

        @Override
        public R getOrElse(Supplier<R> value) {
            return partialValue.orElseGet(value);
        }

        @Override
        public DataResult<R> orElse(Supplier<R> value) {
            return DataResult.success(value.get());
        }

        public <T> DataResult<T> cast() {
            return DataResult.error(messageSupplier);
        }

        @Override
        public @NotNull String toString() {
            return "{DataResult.Error[" + getMessage() + partialValue.map(r -> ": " + r + "]").orElse("]}");
        }

        private static String mergeMessages(String first, String second) {
            return first + "; " + second;
        }
    }
}
