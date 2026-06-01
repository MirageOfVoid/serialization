package net.plugins.serialization.ops;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.plugins.serialization.DataResult;

import java.util.List;
import java.util.Map;

public class JsonOps implements DynamicOps<JsonElement> {
    public static final DynamicOps<JsonElement> INSTANCE = new JsonOps();

    private JsonOps() {}

    @Override
    public JsonElement createNumber(Number value) {
        return new JsonPrimitive(value);
    }

    @Override
    public JsonElement createBool(Boolean value) {
        return new JsonPrimitive(value);
    }

    @Override
    public JsonElement createString(String value) {
        return new JsonPrimitive(value);
    }

    @Override
    public DataResult<JsonElement> mergeToList(JsonElement list, List<JsonElement> value) {
        if (list.isJsonArray()) {
            JsonArray array = list.getAsJsonArray();
            value.forEach(array::add);
            return DataResult.success(array);
        }
        JsonArray array = new JsonArray();
        value.forEach(array::add);
        return DataResult.error(() -> "Not a json array", array);
    }

    @Override
    public DataResult<JsonElement> mergeToMap(JsonElement map, Map<String, JsonElement> value) {
        if (map.isJsonObject()) {
            JsonObject object = map.getAsJsonObject();
            value.forEach(object::add);
            return DataResult.success(object);
        }
        JsonObject object = new JsonObject();
        value.forEach(object::add);
        return DataResult.error(() -> "Not a json object", object);
    }

    @Override
    public DataResult<Number> getNumber(JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return DataResult.success(element.getAsNumber());
        }
        return DataResult.error("Not a string input");
    }

    @Override
    public DataResult<Boolean> getBool(JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            return DataResult.success(element.getAsBoolean());
        }
        return DataResult.error("Not a boolean input");
    }

    @Override
    public DataResult<String> getString(JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return DataResult.success(element.getAsString());
        }
        return DataResult.error("Not a string input");
    }

    @Override
    public DataResult<List<JsonElement>> getList(JsonElement element) {
        if (element.isJsonArray()) {
            return DataResult.success(element.getAsJsonArray().asList());
        }
        return DataResult.error("Not a json array");
    }

    @Override
    public DataResult<Map<String, JsonElement>> getMap(JsonElement element) {
        if (element.isJsonObject()) {
            return DataResult.success(element.getAsJsonObject().asMap());
        }
        return DataResult.error("Not a json object");
    }

    @Override
    public JsonElement createMap(Map<String, JsonElement> value) {
        JsonObject object = new JsonObject();
        value.forEach(object::add);
        return object;
    }

    @Override
    public JsonElement createList(List<JsonElement> value) {
        JsonArray array = new JsonArray();
        value.forEach(array::add);
        return array;
    }
}
