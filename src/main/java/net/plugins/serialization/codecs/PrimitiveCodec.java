package net.plugins.serialization.codecs;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.plugins.serialization.DataResult;

public interface PrimitiveCodec<R> extends Codec<R> {

    DataResult<JsonPrimitive> write(R input);
    DataResult<R> read(JsonPrimitive element);

    /// @deprecated : unsafe
    @Deprecated
    @Override
    default DataResult<JsonElement> encode(R input) {
        return write(input).map(jsonPrimitive -> jsonPrimitive);
    }

    /// @deprecated : unsafe
    @Deprecated
    @Override
    default DataResult<R> decode(JsonElement element) {
        if (element == null || element.isJsonNull())
            return DataResult.error(() -> "Null json input");
        return read(element.getAsJsonPrimitive());
    }
}
