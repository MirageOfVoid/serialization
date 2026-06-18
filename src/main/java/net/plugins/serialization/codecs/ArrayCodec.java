package net.plugins.serialization.codecs;

import net.plugins.serialization.DataResult;
import net.plugins.serialization.DynamicOps;
import net.plugins.util.Pair;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class ArrayCodec<E> implements Codec<E[]> {
    private final Codec<List<E>> codec;
    private final Class<E> elementType;

    @SuppressWarnings("unchecked")
    @Override
    public <T> DataResult<Pair<E[], T>> decode(DynamicOps<T> ops, T input) {
        return codec.decode(ops, input).map(pair -> pair.mapFirst(list -> list.toArray(i -> (E[]) Array.newInstance(elementType, i))));
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, E[] input, T prefix) {
        return codec.encode(ops, Arrays.stream(input).toList(), prefix);
    }
    
    public ArrayCodec(Codec<List<E>> listCodec, Class<E> elementType) {
        this.codec = listCodec;
        this.elementType = elementType;
    }
    
    public ArrayCodec(Class<E> elementType, Codec<E> elementCodec) {
        this(elementCodec.listOf(), elementType);
    }
}
