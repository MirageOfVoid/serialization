package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.ops.DynamicOps;

import java.util.ArrayList;
import java.util.List;

public class ListCodec<E> implements Codec<List<E>> {
    private final Codec<E> codec;

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, List<E> input) {
        DynamicOps.ListBuilder<T> builder = ops.listBuilder();
        for (E e : input) {
            try {
                builder.add(codec.encode(ops, e).getOrThrow());
            } catch (RuntimeException ex) {
                return DataResult.error(() -> "(" + this + "): " + ex.getMessage(), builder.build());
            }
        }
        return DataResult.success(builder.build());
    }

    @Override
    public <T> DataResult<List<E>> decode(DynamicOps<T> ops, T t) {
        List<T> list;

        try {
            list = ops.getList(t).getOrThrow();
        } catch (RuntimeException e) {
            return DataResult.error(e);
        }

        List<E> result = new ArrayList<>();

        for (T t1 : list) {
            try {
                result.add(codec.decode(ops, t1).getOrThrow());
            } catch (RuntimeException e) {
                return DataResult.error(() -> "(" + this + "): " + e.getMessage(), result);
            }
        }

        return DataResult.success(result);
    }

    ListCodec(Codec<E> codec) {
        this.codec = codec;
    }

    @Override
    public String toString() {
        return codec + "[list]";
    }
}
