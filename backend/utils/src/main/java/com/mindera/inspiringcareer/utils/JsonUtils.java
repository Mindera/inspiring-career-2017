package com.mindera.inspiringcareer.utils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class JsonUtils {
    private static final JsonObject EMPTY_JSON_OBJECT = new JsonObject(Collections.emptyMap());
    private static final JsonArray EMPTY_JSON_ARRAY = new JsonArray(Collections.emptyList());

    private JsonUtils() {
    }

    public static JsonObject emptyJsonObject() {
        return EMPTY_JSON_OBJECT;
    }

    public static JsonArray emptyJsonArray() {
        return EMPTY_JSON_ARRAY;
    }

    public static <T> List<T> toList(final JsonArray array, final Class<T> clazz) {
        return array.stream()
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    public static boolean isNullOrEmpty(final JsonObject object) {
        return object == null || object.isEmpty();
    }

    public static boolean isNullOrEmpty(final JsonArray object) {
        return object == null || object.isEmpty();
    }

    public static Collector<Object, JsonArray, JsonArray> toJsonArray() {
        return Collector.of(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }
}
