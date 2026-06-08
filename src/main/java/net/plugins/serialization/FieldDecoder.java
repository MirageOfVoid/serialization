package net.plugins.serialization;

import net.plugins.util.MapLike;

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
    public FieldDecoder(Decoder<R> decoder, String name) {
        this.decoder = decoder;
        this.name = name;
    }

    @Override
    public String toString() {
        return "FieldOf(" + name + ")-FROM";
    }

    public static String getFieldName(MapDecoder<?> mapDecoder) {
        String s = mapDecoder.toString();
        @SuppressWarnings("all")
        String s1 = s.substring(8).replaceAll(")-FROM".toString(), "");
        return s1;
    }
}
