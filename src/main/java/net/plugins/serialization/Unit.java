package net.plugins.serialization;

import net.plugins.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Unit<T> {
    private final T value;
    private final DynamicOps<T> ops;

    public Unit(DynamicOps<T> ops, T value) {
        this.value = value;
        this.ops = ops;
    }

    public Unit(DynamicOps<T> ops) {
        this(ops, ops.empty());
    }

    public T getValue() {
        return value;
    }

    public DynamicOps<T> getOps() {
        return ops;
    }

    public Unit<T> map(Function<T, T> mapper) {
        return new Unit<>(ops, mapper.apply(value));
    }

    public DataResult<String> asString() {
        return ops.getString(value);
    }

    public DataResult<Boolean> asBoolean() {
        return ops.getBool(value);
    }

    public DataResult<Number> asNumber() {
        return ops.getNumber(value);
    }

    public DataResult<Stream<Unit<T>>> asStream() {
        return ops.getList(value).map(list -> list.stream().map(t -> new Unit<>(ops, t)));
    }

    public <E> DataResult<List<E>> asList(Function<Unit<T>, E> elementDeserializer) {
        return asStream().map(stream -> stream.map(elementDeserializer).collect(Collectors.toList()));
    }

    public <K, V> DataResult<Map<K, V>> asMap(Function<Unit<T>, K> keyDeserializer, Function<Unit<T>, V> valueDeserializer) {
        return asMap().map(map -> {
            Map<K, V> map1 = new HashMap<>();
            map.forEach(pair ->
                    map1.put(keyDeserializer.apply(pair.getFirst()), valueDeserializer.apply(pair.getSecond()))
            );
            return map1;
        });
    }

    public DataResult<Stream<Pair<Unit<T>, Unit<T>>>> asMap() {
        return ops.getMapValues(value).map(s -> s.map(p -> Pair.of(new Unit<>(ops, p.getFirst()), new Unit<>(ops, p.getSecond()))));
    }

    public <A> DataResult<Pair<A, T>> decode(Decoder<A> decoder) {
        return decoder.decode(ops, value);
    }

    public <A> DataResult<A> read(Decoder<A> decoder) {
        return decode(decoder).map(Pair::getFirst);
    }

    public <A> Unit<A> convert(DynamicOps<A> outOps) {
        if (Objects.equals(outOps, ops))
            return new Unit<>(outOps, (A) value);
        return new Unit<>(outOps, ops.convert(outOps, value));
    }
}
