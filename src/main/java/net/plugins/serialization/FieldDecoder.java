package net.plugins.serialization;

import net.plugins.util.MapLike;
import net.plugins.util.Pair;
import net.plugins.util.RecordBuilder;

public class FieldDecoder<R> implements MapDecoder<R> {
    private final Decoder<R> decoder;
    private final String name;

    @Override
    public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
        T t = input.get(name);
        if (t == null) {
            DataResult<R> result1 = decoder.parse(ops, ops.empty());
            if (result1.isSuccess())
                return result1;
            return DataResult.error("No field " + name + " in " + input);
        }
        return decoder.parse(ops, t);
    }

    @Override
    public <T> DataResult<Pair<R, T>> compressedDecode(DynamicOps<T> ops, T t) {
        return decoder.decode(ops, t);
    }

    public FieldDecoder(Decoder<R> decoder, String name) {
        this.decoder = decoder;
        this.name = name;
    }
}
