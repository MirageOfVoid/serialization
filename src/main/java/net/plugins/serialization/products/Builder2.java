package net.plugins.serialization.products;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.plugins.function.F2;
import net.plugins.serialization.CodecBuilder;
import net.plugins.serialization.DataResult;
import net.plugins.serialization.codecs.Codec;
import org.jetbrains.annotations.NotNull;

public record Builder2<A, B, O>(CodecBuilder<O, A> a, CodecBuilder<O, B> b) {
    public Codec<O> apply(F2<A, B, O> constructor) {
        return new Codec<O>() {
            @Override
            public DataResult<JsonElement> encode(O input) {
                try {
                    JsonObject object = new JsonObject();

                    a.codec().appendTo(a.getter().apply(input), object).getOrThrow();
                    b.codec().appendTo(b.getter().apply(input), object).getOrThrow();

                    return DataResult.success(object);
                } catch (RuntimeException e) {
                    return DataResult.error(e.getMessage());
                }
            }

            @Override
            public DataResult<O> decode(JsonElement element) {
                try {
                    JsonObject object = element.getAsJsonObject();

                    if (!object.asMap().containsKey(a.field()) && a.codec().isUnsafe()) {
                        return DataResult.error("Could not find field '" + a.field() + "'");
                    }
                    if (!object.asMap().containsKey(b.field()) && b.codec().isUnsafe()) {
                        return DataResult.error("Could not find field '" + b.field() + "'");
                    }

                    DataResult<A> ra = a.codec().decode(object);
                    DataResult<B> rb = b.codec().decode(object);

                    return DataResult.success(constructor.apply(ra.getOrThrow(), rb.getOrThrow()));
                } catch (RuntimeException e) {
                    return DataResult.error(e.getMessage());
                }
            }

            @Override
            public @NotNull String toString() {
                return "Codec(\n" + a.codec() + ";\n" + b.codec() + ")";
            }
        };
    }
}
