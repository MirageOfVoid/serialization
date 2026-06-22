package net.plugins.serialization.codecs;

import net.plugins.serialization.*;
import net.plugins.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface Codec<R> extends Encoder<R>, Decoder<R> {
    static <R> Codec<R> of(Encoder<R> encoder, Decoder<R> decoder, String name) {
        return new Codec<R>() {
            @Override
            public <T> DataResult<Pair<R, T>> decode(DynamicOps<T> ops, T input) {
                return decoder.decode(ops, input);
            }

            @Override
            public <T> DataResult<T> encode(DynamicOps<T> ops, R input, T prefix) {
                return encoder.encode(ops, input, prefix);
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

    static <F, S> Codec<Pair<F, S>> pairCodec(Codec<F> firstCodec, Codec<S> secondCodec) {
        return new PairCodec<>(firstCodec, secondCodec);
    }

    static <E extends Enum<E>> Codec<E> enumCodec(Supplier<E[]> values) {
        return new EnumCodec<>(values);
    }

    static <K, V> Codec<Map<K, V>> basedMapCodec(Codec<K> keyCodec, Codec<V> valueCodec) {
        return new BasedMapCodec<>(keyCodec, valueCodec);
    }

    default Codec<List<R>> listOf() {
        return new ListCodec<>(this);
    }

    default Codec<R[]> arrayOf(Class<R> elementType) {
        return new ArrayCodec<>(elementType, this);
    }

    default Codec<Optional<R>> optionalOf() {
        return new OptionalCodec<>(this);
    }

    default <S> Codec<S> xmap(Function<R, S> to, Function<S, R> from) {
        return Codec.of(comap(from), map(to), this + "[xmapped]");
    }

    default <S> Codec<S> comapFlatMap(Function<R, DataResult<S>> to, Function<S, R> from) {
        return Codec.of(comap(from), flatMap(to), this + "[comapFlatMapped]");
    }

    default <S> Codec<S> flatComapMap(Function<R, S> to, Function<S,DataResult<R>> from) {
        return Codec.of(flatComap(from), map(to), this + "[flatComapMapped]");
    }

    default <S> Codec<S> flatXmap(Function<R, DataResult<S>> to, Function<S, DataResult<R>> from) {
        return Codec.of(flatComap(from), flatMap(to), this + "[flatXmapped]");
    }

    default Codec<R> validate(Function<R, DataResult<R>> checker) {
        return flatXmap(checker, checker);
    }

    default Codec<R> orElse(R r) {
        return Codec.of(
                this,
                Decoder.super.orElse(r),
                toString() + "[safe]"
        );
    }

    default MapCodec<R> fieldOf(String name) {
        return MapCodec.of(
                Encoder.super.fieldOf(name),
                Decoder.super.fieldOf(name),
                toString()
        );
    }

    default MapCodec<Optional<R>> optionalFieldOf(String name) {
        return optionalFieldOf(name, false);
    }

    default MapCodec<Optional<R>> successfulOptionalFieldOf(String name) {
        return optionalFieldOf(name, true);
    }

    default MapCodec<Optional<R>> optionalFieldOf(String name, boolean successful) {
        return new OptionalFieldCodec<>(this, name, successful);
    }

    default MapCodec<R> optionalFieldOf(String name, boolean successful, R defaultValue) {
        return optionalFieldOf(name, successful).xmap(opt -> opt.orElse(defaultValue), Optional::of);
    }

    default Codec<R> mark(String marker) {
        return of(this, this, toString() + "[" + marker + "]");
    }

    default boolean markedWith(String marker) {
        return toString().contains("[" + marker + "]");
    }

    PrimitiveCodec<Byte> BYTE = new PrimitiveCodec<Byte>() {
        @Override
        public <T> T write(DynamicOps<T> ops, Byte input) {
            return ops.createByte(input);
        }

        @Override
        public <T> DataResult<Byte> read(DynamicOps<T> ops, T input) {
            return ops.getByte(input);
        }

        @Override
        public String toString() {
            return "Byte";
        }
    };

    PrimitiveCodec<Short> SHORT = new PrimitiveCodec<Short>() {
        @Override
        public <T> T write(DynamicOps<T> ops, Short input) {
            return ops.createShort(input);
        }

        @Override
        public <T> DataResult<Short> read(DynamicOps<T> ops, T input) {
            return ops.getShort(input);
        }

        @Override
        public String toString() {
            return "Short";
        }
    };

    PrimitiveCodec<Integer> INT = new PrimitiveCodec<Integer>() {
        @Override
        public <T> T write(DynamicOps<T> ops, Integer input) {
            return ops.createInt(input);
        }

        @Override
        public <T> DataResult<Integer> read(DynamicOps<T> ops, T input) {
            return ops.getInt(input);
        }

        @Override
        public String toString() {
            return "Integer";
        }
    };

    PrimitiveCodec<Long> LONG = new PrimitiveCodec<Long>() {
        @Override
        public <T> T write(DynamicOps<T> ops, Long input) {
            return ops.createLong(input);
        }

        @Override
        public <T> DataResult<Long> read(DynamicOps<T> ops, T input) {
            return ops.getLong(input);
        }

        @Override
        public String toString() {
            return "Long";
        }
    };

    PrimitiveCodec<Float> FLOAT = new PrimitiveCodec<Float>() {
        @Override
        public <T> T write(DynamicOps<T> ops, Float input) {
            return ops.createFloat(input);
        }

        @Override
        public <T> DataResult<Float> read(DynamicOps<T> ops, T input) {
            return ops.getFloat(input);
        }

        @Override
        public String toString() {
            return "Float";
        }
    };

    PrimitiveCodec<Double> DOUBLE = new PrimitiveCodec<Double>() {
        @Override
        public <T> T write(DynamicOps<T> ops, Double input) {
            return ops.createDouble(input);
        }

        @Override
        public <T> DataResult<Double> read(DynamicOps<T> ops, T input) {
            return ops.getDouble(input);
        }

        @Override
        public String toString() {
            return "Double";
        }
    };

    PrimitiveCodec<String> STRING = new PrimitiveCodec<String>() {
        @Override
        public <T> T write(DynamicOps<T> ops, String input) {
            return ops.createString(input);
        }

        @Override
        public <T> DataResult<String> read(DynamicOps<T> ops, T input) {
            return ops.getString(input);
        }

        @Override
        public String toString() {
            return "String";
        }
    };

    PrimitiveCodec<Boolean> BOOL = new PrimitiveCodec<Boolean>() {
        @Override
        public <T> T write(DynamicOps<T> ops, Boolean input) {
            return ops.createBool(input);
        }

        @Override
        public <T> DataResult<Boolean> read(DynamicOps<T> ops, T input) {
            return ops.getBool(input);
        }

        @Override
        public String toString() {
            return "Boolean";
        }
    };

    Codec<Unit<?>> PASSTHROUGH = new Codec<Unit<?>>() {
        @Override
        public <T> DataResult<Pair<Unit<?>, T>> decode(DynamicOps<T> ops, T input) {
            return DataResult.success(Pair.of(new Unit<>(ops, input), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(DynamicOps<T> ops, Unit<?> input, T prefix) {
            if (input.getValue() == input.getOps().empty())
                return DataResult.success(prefix);

            T casted = input.convert(ops).getValue();
            if (prefix == ops.empty())
                return DataResult.success(casted);

            DataResult<T> toMap = ops.getMap(casted).flatMap(map -> ops.mergeToMap(prefix, map));
            return toMap.result().map(DataResult::success).orElseGet(() -> {
                DataResult<T> toList = ops.getStream(casted).flatMap(stream -> ops.mergeToList(prefix, stream.collect(Collectors.toList())));
                return toList.result().map(DataResult::success).orElseGet(() ->
                        DataResult.error(() -> "Can not merge " + prefix + " and " + casted, prefix)
                );
            });
        }

        @Override
        public String toString() {
            return "Unit";
        }
    };
}
