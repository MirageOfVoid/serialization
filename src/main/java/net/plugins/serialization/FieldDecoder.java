package net.plugins.serialization;

import net.plugins.util.MapLike;

public class FieldDecoder<R> implements MapDecoder<R> {
    private final Decoder<R> decoder;
    private final String name;

    @Override
    public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
        T t = input.get(name);
        if (t == null) {
            return DataResult.error(() -> "No field \"" + name + "\" in " + input);
        }
        return decoder.parse(ops, t);
    }
    public FieldDecoder(Decoder<R> decoder, String name) {
        this.decoder = decoder;
        this.name = name;
    }
}
