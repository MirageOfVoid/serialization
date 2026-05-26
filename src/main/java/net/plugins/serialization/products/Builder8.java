package net.plugins.serialization.products;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.plugins.function.F8;
import net.plugins.serialization.CodecBuilder;
import net.plugins.serialization.DataResult;
import net.plugins.serialization.codecs.Codec;
import org.jetbrains.annotations.NotNull;

public record Builder8<A, B, C, D, E, F, G, H, O>(CodecBuilder<O, A> a, CodecBuilder<O, B> b, CodecBuilder<O, C> c, CodecBuilder<O, D> d, CodecBuilder<O, E> e,
                                               CodecBuilder<O, F> f, CodecBuilder<O, G> g, CodecBuilder<O, H> h) {
    public Codec<O> apply(F8<A, B, C, D, E, F, G, H, O> constructor, String name) {
        return new Codec<O>() {
            @Override
            public DataResult<JsonElement> encode(O input) {
                JsonObject object = new JsonObject();

                try {
                    a.codec().appendTo(a.getter().apply(input), object).getOrThrow();
                    b.codec().appendTo(b.getter().apply(input), object).getOrThrow();
                    c.codec().appendTo(c.getter().apply(input), object).getOrThrow();
                    d.codec().appendTo(d.getter().apply(input), object).getOrThrow();
                    e.codec().appendTo(e.getter().apply(input), object).getOrThrow();
                    f.codec().appendTo(f.getter().apply(input), object).getOrThrow();
                    g.codec().appendTo(g.getter().apply(input), object).getOrThrow();
                    h.codec().appendTo(h.getter().apply(input), object).getOrThrow();
                } catch (RuntimeException ex) {
                    return DataResult.error(() -> "{%s} ".formatted(this) + ex.getMessage(), object);
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
                    E re = e.codec().decode(object).getOrThrow();
                    F rf = f.codec().decode(object).getOrThrow();
                    G rg = g.codec().decode(object).getOrThrow();
                    H rh = h.codec().decode(object).getOrThrow();

                    return DataResult.success(constructor.apply(ra, rb, rc, rd, re, rf, rg, rh));
                } catch (RuntimeException e) {
                    return DataResult.error(() -> "{%s} ".formatted(this) + e.getMessage());
                }
            }

            @Override
            public @NotNull String toString() {
                return name;
            }
        };
    }

    public Codec<O> apply(F8<A, B, C, D, E, F, G, H, O> constructor) {
        return apply(constructor, "Codec(\n" + a.codec() + ";\n" + b.codec() + ";\n" + c.codec() + ";\n" + d.codec() + ";\n" + e.codec() +
                ";\n" + f.codec() + ";\n" + g.codec() + ";\n" + h.codec() + ")");
    }
}
