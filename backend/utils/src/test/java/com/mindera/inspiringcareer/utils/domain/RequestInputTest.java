package com.mindera.inspiringcareer.utils.domain;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RequestInputTest {

    @Test(expected = IllegalArgumentException.class)
    public void emptyParams() throws Exception {
        final RequestInput requestInput = new RequestInput.Builder()
                .withParams(MultiMap.caseInsensitiveMultiMap())
                .build();

        assertTrue(requestInput.getParams().isEmpty());

        requestInput.getParam("key");
    }

    @Test
    public void param() throws Exception {
        final MultiMap params = MultiMap.caseInsensitiveMultiMap().add("key", "value");
        final RequestInput requestInput = new RequestInput.Builder()
                .withParams(params)
                .build();

        assertEquals(1, requestInput.getParams().size());

        assertEquals("value", requestInput.getParam("key"));
    }

    @Test
    public void params() throws Exception {
        final MultiMap params = MultiMap.caseInsensitiveMultiMap()
                .add("key", "value")
                .add("key2", "value2");
        final RequestInput requestInput = new RequestInput.Builder()
                .withParams(params)
                .build();

        assertEquals(2, requestInput.getParams().size());

        assertEquals("value", requestInput.getParam("key"));
        assertEquals("value2", requestInput.getParam("key2"));
    }

    @Test
    public void testBuilder() throws Exception {
        final RequestInput requestInput = new RequestInput.Builder()
                .withMethod(HttpMethod.GET)
                .withAbsoluteURI("an absolute uri")
                .withHeaders(MultiMap.caseInsensitiveMultiMap().add("header", "value"))
                .withHost("a host")
                .withUri("an uri")
                .withScheme("a scheme")
                .withQuery("a query")
                .withBody(new JsonObject().put("key", "value"))
                .build();

        assertEquals(HttpMethod.GET, requestInput.getMethod());
        assertEquals("an absolute uri", requestInput.getAbsoluteURI());
        assertEquals("value", requestInput.getHeaders().get("header"));
        assertEquals("value", requestInput.getHeader("header"));
        assertEquals("a host", requestInput.getHost());
        assertEquals("an uri", requestInput.getUri());
        assertEquals("a scheme", requestInput.getScheme());
        assertEquals("a query", requestInput.getQuery());
    }
}
