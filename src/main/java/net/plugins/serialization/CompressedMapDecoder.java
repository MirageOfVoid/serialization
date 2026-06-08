package net.plugins.serialization;

import net.plugins.util.MapLike;
import net.plugins.util.Pair;

public class CompressedMapDecoder<R> implements Decoder<R> {
    private final MapDecoder<R> decoder;

    @Override
    public <T> DataResult<Pair<R, T>> decode(DynamicOps<T> ops, T input) {
        if (!ops.isMap(input))
            return DataResult.error("Not a map input: " + input);
        MapLike<T> mapLike = ops.getMap(input).getOrThrow();
        return decoder.decode(ops, mapLike).map(r -> Pair.of(r, input));
    }

    public CompressedMapDecoder(MapDecoder<R> mapDecoder) {
        this.decoder = mapDecoder;
    }
}
