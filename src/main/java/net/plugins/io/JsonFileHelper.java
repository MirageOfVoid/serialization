package net.plugins.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.plugins.serialization.DataResult;
import net.plugins.serialization.JsonOps;
import net.plugins.serialization.codecs.Codec;
import net.plugins.serialization.codecs.MapCodec;
import net.plugins.util.Mapper;

import java.util.Objects;

public class JsonFileHelper<R> extends FileHelper<R, JsonElement> {
    public JsonFileHelper(Codec<R> codec) {
        super(codec, JsonOps.INSTANCE, MAPPER);  
    }

    public JsonFileHelper(MapCodec<R> codec) {
        this(codec.compress());
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Mapper<String, JsonElement> MAPPER = new Mapper<>(element -> DataResult.success(GSON.toJson(element)),
                        str -> DataResult.success(Objects.requireNonNullElse(GSON.fromJson(str, JsonElement.class), JsonNull.INSTANCE))));

}
