package net.plugins.serialization.products;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.plugins.function.F4;
import net.plugins.serialization.CodecBuilder;
import net.plugins.serialization.DataResult;
import net.plugins.serialization.codecs.Codec;
import org.jetbrains.annotations.NotNull;

public record Builder4<A, B, C, D, O>(CodecBuilder<O, A> a, CodecBuilder<O, B> b, CodecBuilder<O, C> c, CodecBuilder<O, D> d) {
    public Codec<O> apply(F4<A, B, C, D, O> constructor) {
        return new Codec<O>() {
            @Override
            public DataResult<JsonElement> encode(O input) {
                JsonObject object = new JsonObject();

                try {
                    a.codec().appendTo(a.getter().apply(input), object).getOrThrow();
                    b.codec().appendTo(b.getter().apply(input), object).getOrThrow();
                    c.codec().appendTo(c.getter().apply(input), object).getOrThrow();
                    d.codec().appendTo(d.getter().apply(input), object).getOrThrow();
                } catch (RuntimeException e) {
                    return DataResult.error(e::getMessage, object);
                }

                return DataResult.success(object);
            }

            @Override
            public DataResult<O> decode(JsonElement element) {
                try {
                    JsonObject object = element.getAsJsonObject();

                    A ra = a.codec().decode(object).getOrThrow();
                    B rb = b.codec().decode(object).getOrThrow();
                    C rc = c.codec().decode(object).getOrThrow();
                    D rd = d.codec().decode(object).getOrThrow();

                    return DataResult.success(constructor.apply(ra, rb, rc, rd));
                } catch (RuntimeException e) {
                    return DataResult.error(e.getMessage());
                }
            }

            @Override
            public @NotNull String toString() {
                return "Codec(\n" + a.codec() + ";\n" + b.codec() + ";\n" + c.codec() + ";\n" + d.codec() + ")";
            }
        };
    }
}
