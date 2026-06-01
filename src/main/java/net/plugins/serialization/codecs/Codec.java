package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.Decoder;
import net.plugins.serialization.Encoder;
import net.plugins.serialization.ops.DynamicOps;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Codec<R> extends Encoder<R>, Decoder<R> {
    @Override
    <T> DataResult<T> encode(DynamicOps<T> ops, R input);

    @Override
    <T> DataResult<R> decode(DynamicOps<T> ops, T t);

    static <R> Codec<R> of(Encoder<R> encoder, Decoder<R> decoder, String name) {
        return new Codec<R>() {
            @Override
            public <T> DataResult<T> encode(DynamicOps<T> ops, R input) {
                return encoder.encode(ops, input);
            }

            @Override
            public <T> DataResult<R> decode(DynamicOps<T> ops, T t) {
                return decoder.decode(ops, t);
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

    static <E extends Enum<E>> Codec<E> enumCodec(Supplier<E[]> values) {
        return new EnumCodec<>(values);
    }

    default Codec<List<R>> listOf() {
        return new ListCodec<>(this);
    }

    default Codec<Map<String, R>> mapOf() {
        return new MapCodec<>(this);
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
        return of(this, new Decoder<R>() {
            @Override
            public <T> DataResult<R> decode(DynamicOps<T> ops, T t) {
                return Codec.this.decode(ops, t).orElse(() -> value);
            }
        }, this + "[safe]");
    }

    default Codec<R> mark(String marker) {
        return of(this, this, this + "[" + marker + "]");
    }

    default boolean markedWith(String marker) {
        return this.toString().contains("[" + marker + "]");
    }

    default FieldCodec<R> fieldOf(String name) {
        return new FieldCodec<>(name, this);
    }

    Codec<Integer> INT = new Codec<Integer>() {
        @Override
        public <T> DataResult<T> encode(DynamicOps<T> ops, Integer input) {
            return DataResult.success(ops.createInt(input));
        }

        @Override
        public <T> DataResult<Integer> decode(DynamicOps<T> ops, T t) {
            return ops.getInt(t);
        }

        @Override
        public String toString() {
            return "Integer";
        }
    };

    Codec<Float> FLOAT = new Codec<Float>() {
        @Override
        public <T> DataResult<T> encode(DynamicOps<T> ops, Float input) {
            return DataResult.success(ops.createFloat(input));
        }

        @Override
        public <T> DataResult<Float> decode(DynamicOps<T> ops, T t) {
            return ops.getFloat(t);
        }

        @Override
        public String toString() {
            return "Float";
        }
    };

    Codec<Double> DOUBLE = new Codec<Double>() {
        @Override
        public <T> DataResult<T> encode(DynamicOps<T> ops, Double input) {
            return DataResult.success(ops.createDouble(input));
        }

        @Override
        public <T> DataResult<Double> decode(DynamicOps<T> ops, T t) {
            return ops.getDouble(t);
        }

        @Override
        public String toString() {
            return "Double";
        }
    };

    Codec<Byte> BYTE = new Codec<Byte>() {
        @Override
        public <T> DataResult<T> encode(DynamicOps<T> ops, Byte input) {
            return DataResult.success(ops.createByte(input));
        }

        @Override
        public <T> DataResult<Byte> decode(DynamicOps<T> ops, T t) {
            return ops.getByte(t);
        }

        @Override
        public String toString() {
            return "Byte";
        }
    };

    Codec<Boolean> BOOL = new Codec<Boolean>() {
        @Override
        public <T> DataResult<T> encode(DynamicOps<T> ops, Boolean input) {
            return DataResult.success(ops.createBool(input));
        }

        @Override
        public <T> DataResult<Boolean> decode(DynamicOps<T> ops, T t) {
            return ops.getBool(t);
        }

        @Override
        public String toString() {
            return "Boolean";
        }
    };

    Codec<String> STRING = new Codec<String>() {
        @Override
        public <T> DataResult<T> encode(DynamicOps<T> ops, String input) {
            return DataResult.success(ops.createString(input));
        }

        @Override
        public <T> DataResult<String> decode(DynamicOps<T> ops, T t) {
            return ops.getString(t);
        }

        @Override
        public String toString() {
            return "String";
        }
    };
}
