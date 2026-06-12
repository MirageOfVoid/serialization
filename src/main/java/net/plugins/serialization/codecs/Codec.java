package net.plugins.serialization.codecs;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.plugins.serialization.DataResult;
import net.plugins.serialization.Decoder;
import net.plugins.serialization.Encoder;
import net.plugins.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Codec<R> extends Encoder<R>, Decoder<R> {
    @Override
    DataResult<JsonElement> encode(R input);

    @Override
    DataResult<R> decode(JsonElement element);

    static <R> Codec<R> of(Encoder<R> encoder, Decoder<R> decoder, String name) {
        return new Codec<R>() {
            @Override
            public DataResult<R> decode(JsonElement element) {
                return decoder.decode(element);
            }

            @Override
            public DataResult<JsonElement> encode(R input) {
                return encoder.encode(input);
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    static <R> Codec<R> of(Encoder<R> encoder, Decoder<R> decoder) {
        return of(encoder, decoder, "Codec");
    }

    static <R> Codec<R> simple(Class<R> type) {
        return new SimpleCodec<>(type);
    }

    static <F, S> Codec<Pair<F, S>> pairCodec(Codec<F> firstCodec, Codec<S> secondCodec) {
        return new PairCodec<>(firstCodec, secondCodec);
    }

    static <E extends Enum<E>> Codec<E> enumCodec(Supplier<? extends E[]> values) {
        return new EnumCodec<>(values.get());
    }

    default Codec<List<R>> listOf() {
        return new ListCodec<>(this);
    }

    default Codec<R[]> arrayOf(Class<R> type) {
        return new ArrayCodec<>(this, type);
    }

    default Codec<Stream<R>> streamOf() {
        return new StreamCodec<>(this);
    }

    default Codec<Map<String, R>> mapOf() {
        return new MapCodec<>(this);
    }

    default Codec<Optional<R>> optionalOf() {
        return new OptionalCodec<>(this);
    }

    default <T> Codec<T> xmap(Function<R, T> to, Function<T, R> from) {
        return of(comap(from), map(to), this + "[xmapped]");
    }

    default <T> Codec<T> comapFlatMap(Function<R, DataResult<T>> to, Function<T, R> from) {
        return of(comap(from), flatMap(to), this + "[comapFlatMapped]");
    }

    default <T> Codec<T> flatComapMap(Function<R, T> to, Function<T, DataResult<R>> from) {
        return of(flatComap(from), map(to), this + "[flatComapMapped]");
    }

    default <T> Codec<T> flatXmap(Function<R, DataResult<T>> to, Function<T, DataResult<R>> from) {
        return of(flatComap(from), flatMap(to), this + "[flatXmapped]");
    }

    default Codec<R> validate(Function<R, DataResult<R>> function) {
        return flatXmap(function, function);
    }

    default Codec<R> orElse(R value) {
        return of(this, element -> Codec.this.decode(element).orElse(() -> value), this + "[safe]");
    }

    default Codec<R> mark(String marker) {
        return of(this, this, this + "[" + marker + "]");
    }

    default boolean markedWith(String marker) {
        return this.toString().contains("[" + marker + "]");
    }

    default FieldCodec<R> fieldOf(String field) {
        return new FieldCodec<>(field, this);
    }

    default FieldCodec<Optional<R>> optionalFieldOf(String field) {
        return new FieldCodec<>(field, this.optionalOf());
    }

    PrimitiveCodec<Integer> INT = new PrimitiveCodec<Integer>() {
        @Override
        public DataResult<JsonPrimitive> write(Integer input) {
            return DataResult.success(new JsonPrimitive(input));
        }

        @Override
        public DataResult<Integer> read(JsonPrimitive element) {
            if (element.isJsonNull())
                return DataResult.error("(%s) Null value".formatted(this));

            return DataResult.success(element.getAsInt());
        }

        @Override
        public String toString() {
            return "Int";
        }
    };

    PrimitiveCodec<Float> FLOAT = new PrimitiveCodec<Float>() {
        @Override
        public DataResult<JsonPrimitive> write(Float input) {
            return DataResult.success(new JsonPrimitive(input));
        }

        @Override
        public DataResult<Float> read(JsonPrimitive element) {
            if (element.isJsonNull())
                return DataResult.error("(%s) Null value".formatted(this));

            return DataResult.success(element.getAsFloat());
        }

        @Override
        public String toString() {
            return "Float";
        }
    };

    PrimitiveCodec<Double> DOUBLE = new PrimitiveCodec<Double>() {
        @Override
        public DataResult<JsonPrimitive> write(Double input) {
            return DataResult.success(new JsonPrimitive(input));
        }

        @Override
        public DataResult<Double> read(JsonPrimitive element) {
            if (element.isJsonNull())
                return DataResult.error("(%s) Null value".formatted(this));

            return DataResult.success(element.getAsDouble());
        }

        @Override
        public String toString() {
            return "Double";
        }
    };

    PrimitiveCodec<String> STRING = new PrimitiveCodec<String>() {
        @Override
        public DataResult<JsonPrimitive> write(String input) {
            return DataResult.success(new JsonPrimitive(input));
        }

        @Override
        public DataResult<String> read(JsonPrimitive element) {
            if (element.isJsonNull())
                return DataResult.error("(%s) Null value".formatted(this));

            return DataResult.success(element.getAsString());
        }

        @Override
        public String toString() {
            return "String";
        }
    };

    PrimitiveCodec<Boolean> BOOL = new PrimitiveCodec<Boolean>() {
        @Override
        public DataResult<JsonPrimitive> write(Boolean input) {
            return DataResult.success(new JsonPrimitive(input));
        }

        @Override
        public DataResult<Boolean> read(JsonPrimitive element) {
            if (element.isJsonNull())
                return DataResult.error("(%s) Null value".formatted(this));

            return DataResult.success(element.getAsBoolean());
        }

        @Override
        public String toString() {
            return "Boolean";
        }
    };
}
