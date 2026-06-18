package net.plugins.serialization;

import com.google.gson.*;
import net.plugins.util.ListBuilder;
import net.plugins.util.MapLike;
import net.plugins.util.Pair;
import net.plugins.util.RecordBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public JsonElement createList(List<JsonElement> value) {
        JsonArray array = new JsonArray();
        value.forEach(array::add);
        return array;
    }

    @Override
    public JsonElement createMap(MapLike<JsonElement> value) {
        JsonObject object = new JsonObject();
        value.entries().forEach(pair -> object.add(pair.getFirst().getAsString(), pair.getSecond()));
        return object;
    }

    @Override
    public JsonElement createMap(Stream<Pair<JsonElement, JsonElement>> map) {
        JsonObject object = new JsonObject();
        map.forEach(pair -> object.add(pair.getFirst().getAsString(), pair.getSecond()));
        return object;
    }

    @Override
    public JsonElement createList(Stream<JsonElement> list) {
        JsonArray array = new JsonArray();
        list.forEach(array::add);
        return array;
    }

    @Override
    public DataResult<JsonElement> mergeToList(JsonElement list, List<JsonElement> value) {
        if (list.isJsonArray()) {
            JsonArray array = list.getAsJsonArray();
            array.addAll(createList(value).getAsJsonArray());
            return DataResult.success(array);
        }
        return DataResult.error("Not a json array: " + list);
    }

    @Override
    public DataResult<JsonElement> mergeToMap(JsonElement map, MapLike<JsonElement> value) {
        if (map.isJsonObject()) {
            JsonObject object = map.getAsJsonObject();
            value.entries().forEach(pair -> object.add(pair.getFirst().getAsString(), pair.getSecond()));
        }
        return DataResult.error("Not a json object: " + map);
    }

    @Override
    public DataResult<Number> getNumber(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber())
                return DataResult.success(primitive.getAsNumber());
            return DataResult.error("Not a number: " + primitive);
        }
        return DataResult.error("Not a json primitive: " + element);
    }

    @Override
    public DataResult<Boolean> getBool(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean())
                return DataResult.success(primitive.getAsBoolean());
            return DataResult.error("Not a boolean: " + primitive);
        }
        return DataResult.error("Not a json primitive: " + element);
    }

    @Override
    public DataResult<String> getString(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString())
                return DataResult.success(primitive.getAsString());
            return DataResult.error("Not a string: " + primitive);
        }
        return DataResult.error("Not a json primitive: " + element);
    }

    @Override
    public DataResult<List<JsonElement>> getList(JsonElement element) {
        if (element.isJsonArray()) {
            return DataResult.success(element.getAsJsonArray().asList());
        }
        return DataResult.error("Not a json array: " + element);
    }

    @Override
    public DataResult<MapLike<JsonElement>> getMap(JsonElement element) {
        if (element.isJsonObject()) {
            return DataResult.success(
                    MapLike.of(
                            element.getAsJsonObject().entrySet().stream().collect(
                                    Collectors.toMap(entry -> createString(entry.getKey()), Map.Entry::getValue)
                            ), this
                    )
            );
        }
        return DataResult.error("Not a json object: " + element);
    }

    @Override
    public DataResult<Stream<Pair<JsonElement, JsonElement>>> getMapValues(JsonElement element) {
        if (element.isJsonObject())
            return DataResult.success(element.getAsJsonObject().entrySet().stream().map(entry -> Pair.of(new JsonPrimitive(entry.getKey()), entry.getValue().isJsonNull() ? null : entry.getValue())));

        return DataResult.error("Not a json object: " + element);
    }

    @Override
    public DataResult<JsonElement> getFromMap(JsonElement map, String key) {
        DataResult<MapLike<JsonElement>> mapRes = getMap(map);
        return mapRes.flatMap(mapLike -> {

            JsonElement result = mapLike.get(key);
            if (result == null || result.isJsonNull())
                return DataResult.error("No field " + key + " in " + map);
            return DataResult.success(result);
        });
    }

    @Override
    public DataResult<JsonElement> getFromList(JsonElement list, int index) {
        DataResult<List<JsonElement>> listRes = getList(list);
        return listRes.flatMap(lst -> {
            if (index >= lst.size())
                return DataResult.error("Index " + index + " out of bounds for length " + lst.size());

            return DataResult.success(lst.get(index));
        });
    }

    @Override
    public JsonElement empty() {
        return JsonNull.INSTANCE;
    }

    @Override
    public JsonElement emptyMap() {
        return new JsonObject();
    }

    @Override
    public JsonElement emptyList() {
        return new JsonArray();
    }

    @Override
    public <U> U convert(DynamicOps<U> ops, JsonElement element) {
        if (element.isJsonObject())
            return convertMap(ops, element);
        if (element.isJsonArray())
            return convertList(ops, element);
        if (element.isJsonNull())
            return ops.empty();

        JsonPrimitive primitive = element.getAsJsonPrimitive();

        if (primitive.isString())
            return ops.createString(primitive.getAsString());
        if (primitive.isBoolean())
            return ops.createBool(primitive.getAsBoolean());

        return ops.createNumber(primitive.getAsNumber());
    }

    @Override
    public boolean isMap(JsonElement element) {
        return element.isJsonObject();
    }

    @Override
    public boolean isList(JsonElement element) {
        return element.isJsonArray();
    }

    @Override
    public boolean isPrimitive(JsonElement element) {
        return element.isJsonPrimitive();
    }

    @Override
    public boolean isEmpty(JsonElement element) {
        return element.isJsonNull();
    }

    @Override
    public ListBuilder<JsonElement> listBuilder() {
        return new JsonArrayBuilder(INSTANCE);
    }

    @Override
    public RecordBuilder<JsonElement> mapBuilder() {
        return new JsonRecordBuilder(INSTANCE);
    }

    public static final class JsonRecordBuilder extends RecordBuilder.AbstractRecordBuilder<JsonElement, JsonObject> {
        public JsonRecordBuilder(DynamicOps<JsonElement> ops) {
            super(ops);
        }

        @Override
        protected JsonObject initBuilder() {
            return new JsonObject();
        }

        @Override
        protected JsonObject append(String key, JsonElement value, JsonObject builder) {
            builder.add(key, value);
            return builder;
        }

        @Override
        protected DataResult<JsonElement> build(JsonObject builder, JsonElement prefix) {
            if (prefix == null || prefix.isJsonNull())
                return DataResult.success(builder);
            if (prefix.isJsonObject()) {
                JsonObject result = new JsonObject();
                for (Map.Entry<String, JsonElement> entry : builder.entrySet())
                    result.add(entry.getKey(), entry.getValue());
                for (Map.Entry<String, JsonElement> entry : prefix.getAsJsonObject().entrySet())
                    result.add(entry.getKey(), entry.getValue());
                return DataResult.success(result);
            }
            return DataResult.error(() -> "Not a json object: " + prefix, builder);
        }
    }

    public static final class JsonArrayBuilder extends ListBuilder.AbstractListBuilder<JsonElement, JsonArray> {
        public JsonArrayBuilder(DynamicOps<JsonElement> ops) {
            super(ops);
        }

        @Override
        protected JsonArray initBuilder() {
            return new JsonArray();
        }

        @Override
        protected JsonArray append(JsonArray builder, JsonElement element) {
            builder.add(element);
            return builder;
        }

        @Override
        protected DataResult<JsonElement> build(JsonArray builder, JsonElement prefix) {
            if (prefix == null || prefix.isJsonNull()) {
                return DataResult.success(builder);
            }
            if (prefix.isJsonArray()) {
                JsonArray array = new JsonArray();
                array.addAll(builder);
                array.addAll(prefix.getAsJsonArray());
                return DataResult.success(array);
            }
            return DataResult.error("Not a json array: " + prefix);
        }
    }
}
