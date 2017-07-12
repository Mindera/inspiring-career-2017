package com.mindera.inspiringcareer.utils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.List;

import static com.mindera.inspiringcareer.utils.JsonUtils.isNullOrEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JsonUtilsTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyJsonObject() {
        JsonObject empty = JsonUtils.emptyJsonObject();

        empty.put("break", "break");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyJsonArray() {
        JsonArray empty = JsonUtils.emptyJsonArray();

        empty.add("break");
    }

    @Test
    public void testToListEmptyList() {
        final JsonArray array = new JsonArray();

        final List<String> result = JsonUtils.toList(array, String.class);

        assertEquals(0, result.size());
        try {
            result.get(0);
        } catch (Throwable e) {
            assertTrue(e instanceof IndexOutOfBoundsException);
            assertEquals("Index: 0, Size: 0", e.getMessage());
        }
    }

    @Test
    public void testToListOneElement() {
        final JsonArray array = new JsonArray().add("a");

        final List<String> result = JsonUtils.toList(array, String.class);

        assertEquals(1, result.size());
        assertEquals("a", result.get(0));
    }

    @Test
    public void testToListMultipleElements() {
        final JsonArray array = new JsonArray()
                .add("a")
                .add("b")
                .add("c");

        final List<String> result = JsonUtils.toList(array, String.class);

        assertEquals(3, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
        assertEquals("c", result.get(2));
    }

    @Test
    public void testIsNullOrEmptyNullObject() {
        JsonObject nullObject = null;
        assertTrue(JsonUtils.isNullOrEmpty(nullObject));
    }

    @Test
    public void testIsNullOrEmptyNullArray() {
        JsonArray nullArray = null;
        assertTrue(JsonUtils.isNullOrEmpty(nullArray));
    }

    @Test
    public void testIsNullOrEmptyEmptyObject() {
        assertTrue(JsonUtils.isNullOrEmpty(JsonUtils.emptyJsonObject()));
    }

    @Test
    public void testIsNullOrEmptyEmptyArray() {
        assertTrue(JsonUtils.isNullOrEmpty(JsonUtils.emptyJsonArray()));
    }

    @Test
    public void testIsNullOrEmptyObject() {
        assertFalse(JsonUtils.isNullOrEmpty(new JsonObject().put("stuff", true)));
    }

    @Test
    public void testIsNullOrEmptyArray() {
        assertFalse(JsonUtils.isNullOrEmpty(new JsonArray().add("stuff")));
    }
}
