package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;
import net.plugins.util.Pair;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EnumCodec<E extends Enum<E>> implements Codec<E> {
    private final Map<String, E> map;

    @Override
    public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<String> in = ops.getString(input);
        if (in.isError())
            return in.error().get().cast();
        String name = in.getOrThrow();
        E e = map.get(name);
        if (e == null)
            return DataResult.error("Could not find enum element " + name);
        return DataResult.success(Pair.of(e, ops.empty()));
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, E input, T prefix) {
        return ops.mergeToPrimitive(prefix, ops.createString(input.name()));
    }

    public EnumCodec(Supplier<E[]> values) {
        this.map = Arrays.stream(values.get()).collect(Collectors.toMap(Enum::name, e -> e));
    }
}
