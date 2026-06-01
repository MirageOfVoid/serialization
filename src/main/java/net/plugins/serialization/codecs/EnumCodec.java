package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.ops.DynamicOps;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EnumCodec<E extends Enum<E>> implements Codec<E> {
    private final Map<String, E> values;

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, E input) {
        return DataResult.success(ops.createString(input.name()));
    }

    @Override
    public <T> DataResult<E> decode(DynamicOps<T> ops, T t) {
        DataResult<String> in = ops.getString(t);
        if (in.isError())
            return in.error().get().cast();
        String name = in.getOrThrow();
        if (values.containsKey(name)) {
            return DataResult.success(values.get(name));
        }
        return DataResult.error("Could not find enum element named '" + name + "'");
    }

    EnumCodec(Supplier<E[]> values) {
        this.values = Arrays.stream(values.get()).collect(Collectors.toMap(Enum::name, e -> e));
    }
}
