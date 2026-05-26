package net.plugins.serialization.products;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.plugins.function.F5;
import net.plugins.serialization.CodecBuilder;
import net.plugins.serialization.DataResult;
import net.plugins.serialization.codecs.Codec;
import org.jetbrains.annotations.NotNull;

public record Builder5<A, B, C, D, E, O>(CodecBuilder<O, A> a, CodecBuilder<O, B> b, CodecBuilder<O, C> c, CodecBuilder<O, D> d, CodecBuilder<O, E> e) {
    public Codec<O> apply(F5<A, B, C, D, E, O> constructor) {
        return new Codec<O>() {
            @Override
            public DataResult<JsonElement> encode(O input) {
                try {
                    JsonObject object = new JsonObject();

                    a.codec().appendTo(a.getter().apply(input), object).getOrThrow();
                    b.codec().appendTo(b.getter().apply(input), object).getOrThrow();
                    c.codec().appendTo(c.getter().apply(input), object).getOrThrow();
                    d.codec().appendTo(d.getter().apply(input), object).getOrThrow();
                    e.codec().appendTo(e.getter().apply(input), object).getOrThrow();

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
                    if (!object.asMap().containsKey(c.field()) && c.codec().isUnsafe()) {
                        return DataResult.error("Could not find field '" + c.field() + "'");
                    }
                    if (!object.asMap().containsKey(d.field()) && d.codec().isUnsafe()) {
                        return DataResult.error("Could not find field '" + d.field() + "'");
                    }
                    if (!object.asMap().containsKey(e.field()) && e.codec().isUnsafe()) {
                        return DataResult.error("Could not find field '" + e.field() + "'");
                    }

                    A ra = a.codec().decode(object).getOrThrow();
                    B rb = b.codec().decode(object).getOrThrow();
                    C rc = c.codec().decode(object).getOrThrow();
                    D rd = d.codec().decode(object).getOrThrow();
                    E re = e.codec().decode(object).getOrThrow();

                    return DataResult.success(constructor.apply(ra, rb, rc, rd, re));
                } catch (RuntimeException e) {
                    return DataResult.error(e.getMessage());
                }
            }

            @Override
            public @NotNull String toString() {
                return "Codec(\n" + a.codec() + ";\n" + b.codec() + ";\n" + c.codec() + ";\n" + d.codec() + ";\n" + e.codec() + ")";
            }
        };
    }
}
