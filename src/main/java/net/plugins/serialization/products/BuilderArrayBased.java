package net.plugins.serialization.products;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.plugins.serialization.CodecBuilder;
import net.plugins.serialization.DataResult;
import net.plugins.serialization.codecs.Codec;

import java.util.List;
import java.util.function.Function;

public record BuilderArrayBased<O, E>(CodecBuilder<O, List<E>> builder) {
    public Codec<O> apply(Function<List<E>, O> constructor) {
        return new Codec<O>() {
            @Override
            public DataResult<JsonElement> encode(O input) {
                try {
                    List<E> list = builder.getter().apply(input);
                    JsonArray array = builder.codec().select().encode(list).getOrThrow().getAsJsonArray();
                    return DataResult.success(array);
                } catch (RuntimeException e) {
                    return DataResult.error(e);
                }
            }

            @Override
            public DataResult<O> decode(JsonElement element) {
                JsonArray array = element.getAsJsonArray();
                List<E> list = builder.codec().select().decode(array).getOrThrow();
                return DataResult.success(constructor.apply(list));
            }

            @Override
            public String toString() {
                return "Codec](" + builder.codec().select() + ")";
            }
        };
    }
}
