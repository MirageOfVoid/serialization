package net.plugins.serialization;

import net.plugins.kinds.App;
import net.plugins.kinds.Applicative;
import net.plugins.kinds.K1;
import net.plugins.util.function.Function3;
import net.plugins.util.function.Function4;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface DataResult<R> extends App<DataResult.Mu, R> permits DataResult.Success, DataResult.Error {
    final class Mu implements K1 {}

    static <R> DataResult<R> unbox(App<Mu, R> box) {
        return (DataResult<R>) box;
    }

    static <R> DataResult<R> success(R value) {
        return new Success<>(Objects.requireNonNull(value));
    }

    static Instance instance() {
        return Instance.INSTANCE;
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

    static String appendMessages(String first, String second) {
        return first + ";" + second;
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

    <R2> DataResult<R2> ap(DataResult<Function<R, R2>> functionResult);

    default <R2, S> DataResult<S> apply2(BiFunction<R, R2, S> function, DataResult<R2> second) {
        return unbox(instance().apply2(function, this, second));
    }

    default <R2, R3, S> DataResult<S> apply3(Function3<R, R2, R3, S> function, DataResult<R2> second, DataResult<R3> third) {
        return unbox(instance().apply3(function, this, second, third));
    }

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
        public <R2> DataResult<R2> ap(DataResult<Function<R, R2>> functionResult) {
            if (functionResult instanceof Success<Function<R, R2>>(Function<R, R2> value1)) {
                return new Success<>(value1.apply(value));
            } else if (functionResult instanceof Error<Function<R, R2>>(Supplier<String> messageSupplier, Optional<Function<R, R2>> partialValue)) {
                return new Error<>(messageSupplier, partialValue.map(f -> f.apply(value)));
            } else {
                throw new UnsupportedOperationException();
            }
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
            return "{DataResult.Success[" + value + "]}";
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
            if (result instanceof DataResult.Success<T> success) {
                return new Error<>(messageSupplier, Optional.of(success.value()));
            } else if (result instanceof DataResult.Error<T> error) {
                return new Error<>(() -> appendMessages(getMessage(), error.getMessage()), error.partialValue);
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public <R2> Error<R2> ap(final DataResult<Function<R, R2>> functionResult) {
            if (functionResult instanceof final Success<Function<R, R2>> func) {
                return new Error<>(messageSupplier, partialValue.map(func.value));
            } else if (functionResult instanceof final Error<Function<R, R2>> funcError) {
                return new Error<>(
                        () -> appendMessages(messageSupplier.get(), funcError.messageSupplier.get()),
                        partialValue.flatMap(a -> funcError.partialValue.map(f -> f.apply(a)))
                );
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
    }

    enum Instance implements Applicative<Mu, Instance.Mu> {
        INSTANCE;

        @Override
        public <A> App<DataResult.Mu, A> point(A a) {
            return success(a);
        }

        @Override
        public <A, R> Function<App<DataResult.Mu, A>, App<DataResult.Mu, R>> lift(App<DataResult.Mu, Function<A, R>> function) {
            return fa -> ap(function, fa);
        }

        @Override
        public <T, R> App<DataResult.Mu, R> map(Function<T, R> function, App<DataResult.Mu, T> app) {
            return unbox(app).map(function);
        }

        @Override
        public <A, R> App<DataResult.Mu, R> ap(App<DataResult.Mu, Function<A, R>> function, App<DataResult.Mu, A> arg) {
            return unbox(arg).ap(unbox(function));
        }

        @Override
        public <A, B, R> App<DataResult.Mu, R> ap2(final App<DataResult.Mu, BiFunction<A, B, R>> func, final App<DataResult.Mu, A> a, final App<DataResult.Mu, B> b) {
            final DataResult<BiFunction<A, B, R>> fr = unbox(func);
            final DataResult<A> ra = unbox(a);
            final DataResult<B> rb = unbox(b);

            if (fr.result().isPresent()
                    && ra.result().isPresent()
                    && rb.result().isPresent()
            ) {
                return new Success<>(fr.result().get().apply(
                        ra.result().get(),
                        rb.result().get()
                ));
            }

            return Applicative.super.ap2(func, a, b);
        }

        @Override
        public <T1, T2, T3, R> App<DataResult.Mu, R> ap3(final App<DataResult.Mu, Function3<T1, T2, T3, R>> func, final App<DataResult.Mu, T1> t1, final App<DataResult.Mu, T2> t2, final App<DataResult.Mu, T3> t3) {
            final DataResult<Function3<T1, T2, T3, R>> fr = unbox(func);
            final DataResult<T1> dr1 = unbox(t1);
            final DataResult<T2> dr2 = unbox(t2);
            final DataResult<T3> dr3 = unbox(t3);

            if (fr.result().isPresent()
                    && dr1.result().isPresent()
                    && dr2.result().isPresent()
                    && dr3.result().isPresent()
            ) {
                return new Success<>(fr.result().get().apply(
                        dr1.result().get(),
                        dr2.result().get(),
                        dr3.result().get()
                ));
            }

            return Applicative.super.ap3(func, t1, t2, t3);
        }

        @Override
        public <A, B, C, D, R> App<DataResult.Mu, R> ap4(App<DataResult.Mu, Function4<A, B, C, D, R>> func, App<DataResult.Mu, A> a, App<DataResult.Mu, B> b, App<DataResult.Mu, C> c, App<DataResult.Mu, D> d) {
            final DataResult<Function4<A, B, C, D, R>> fr = unbox(func);
            final DataResult<A> da = unbox(a);
            final DataResult<B> db = unbox(b);
            final DataResult<C> dc = unbox(c);
            final DataResult<D> dd = unbox(d);

            if (fr.result().isPresent()
                    && da.result().isPresent()
                    && db.result().isPresent()
                    && dc.result().isPresent()
                    && dd.result().isPresent()
            ) {
                return new Success<>(fr.result().get().apply(
                        da.result().get(),
                        db.result().get(),
                        dc.result().get(),
                        dd.result().get()
                ));
            }

            return Applicative.super.ap4(func, da, db, dc, dd);
        }

        public static final class Mu implements Applicative.Mu {}
    }
}
