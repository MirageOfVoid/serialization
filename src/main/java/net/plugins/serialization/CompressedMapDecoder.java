package net.plugins.serialization;

import net.plugins.util.Pair;

public class CompressedMapDecoder<R> implements Decoder<R> {
    private final MapDecoder<R> decoder;

    @Override
    public <T> DataResult<Pair<R, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMap(input).flatMap(mapLike -> decoder.decode(ops, mapLike).map(r -> Pair.of(r, ops.empty())));
    }

    public CompressedMapDecoder(MapDecoder<R> mapDecoder) {
        this.decoder = mapDecoder;
    }
}
