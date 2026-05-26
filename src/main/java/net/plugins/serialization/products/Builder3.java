package net.plugins.serialization.products;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.plugins.function.F3;
import net.plugins.serialization.CodecBuilder;
import net.plugins.serialization.DataResult;
import net.plugins.serialization.codecs.Codec;
import org.jetbrains.annotations.NotNull;

public record Builder3<A, B, C, O>(CodecBuilder<O, A> a, CodecBuilder<O, B> b, CodecBuilder<O, C> c) {
    public Codec<O> apply(F3<A, B, C, O> constructor) {
        return new Codec<O>() {
            @Override
            public DataResult<JsonElement> encode(O input) {
                try {
                    JsonObject object = new JsonObject();

                    a.codec().appendTo(a.getter().apply(input), object).getOrThrow();
                    b.codec().appendTo(b.getter().apply(input), object).getOrThrow();
                    c.codec().appendTo(c.getter().apply(input), object).getOrThrow();

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

                    DataResult<A> ra = a.codec().decode(object);
                    DataResult<B> rb = b.codec().decode(object);
                    DataResult<C> rc = c.codec().decode(object);

                    return DataResult.success(constructor.apply(ra.getOrThrow(), rb.getOrThrow(), rc.getOrThrow()));
                } catch (RuntimeException e) {
                    return DataResult.error(e.getMessage());
                }
            }

            @Override
            public @NotNull String toString() {
                return "Codec(\n" + a.codec() + ";\n" + b.codec() + ";\n" + c.codec() + ")";
            }
        };
    }
}
