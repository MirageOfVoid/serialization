package net.plugins.serialization.products;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.plugins.serialization.CodecBuilder;
import net.plugins.serialization.DataResult;
import net.plugins.serialization.codecs.Codec;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public record Builder1<A, O>(CodecBuilder<O, A> a) {
    public Codec<O> apply(Function<A, O> constructor, String name) {
        return new Codec<O>() {
            @Override
            public DataResult<JsonElement> encode(O input) {
                JsonObject object = new JsonObject();

                try {
                    a.codec().appendTo(a.getter().apply(input), object).getOrThrow();
                } catch (RuntimeException e) {
                    return DataResult.error(() -> "{%s} ".formatted(this) + e.getMessage(), object);
                }

                return DataResult.success(object);
            }

            @Override
            public DataResult<O> decode(JsonElement element) {
                try {
                    JsonObject object = element.getAsJsonObject();

                    DataResult<A> ra = a.codec().decode(object);

                    return DataResult.success(constructor.apply(ra.getOrThrow()));
                } catch (RuntimeException e) {
                    return DataResult.error("{%s} ".formatted(this) + e.getMessage());
                }
            }

            @Override
            public @NotNull String toString() {
                return name;
            }
        };
    }

    public Codec<O> apply(Function<A, O> constructor) {
        return apply(constructor, "Codec(\n" + a.codec() + ")");
    }
}
