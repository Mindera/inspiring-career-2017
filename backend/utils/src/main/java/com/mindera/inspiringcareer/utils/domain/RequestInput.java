package com.mindera.inspiringcareer.utils.domain;

import com.mindera.inspiringcareer.utils.Loggable;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.util.Optional;

public final class RequestInput implements Loggable {

    private final JsonObject body;
    private final HttpMethod method;
    private final String absoluteURI;
    private final MultiMap headers;
    private final String host;
    private final MultiMap params;
    private final String uri;
    private final String scheme;
    private final String query;

    private RequestInput(final Builder builder) {
        body = builder.body;
        method = builder.method;
        absoluteURI = builder.absoluteURI;
        headers = builder.headers;
        host = builder.host;
        params = builder.params;
        uri = builder.uri;
        scheme = builder.scheme;
        query = builder.query;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getAbsoluteURI() {
        return absoluteURI;
    }

    public MultiMap getHeaders() {
        return headers;
    }

    public String getHeader(final String header) {
        return headers.get(header);
    }

    public String getHeader(final String header, final String defaultValue) {
        final String value = headers.get(header);
        return value != null ? value : defaultValue;
    }

    public String getHost() {
        return host;
    }

    public MultiMap getParams() {
        return params == null ? MultiMap.caseInsensitiveMultiMap() : params;
    }

    @SuppressWarnings("unchecked")
    public <T> T getParam(final String paramKey) {
        final Optional<T> param = getOptionalParam(paramKey);

        if (param.isPresent()) {
            return param.get();
        } else {
            logger().error("Parameter {0} must be present.", paramKey);
            throw new IllegalArgumentException("Parameter " + paramKey + " must be present.");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOptionalParam(final String paramKey) {
        return Optional.ofNullable(getParams().get(paramKey)).map(s -> (T) s);
    }

    @SuppressWarnings("unchecked")
    public Optional<Integer> getOptionalIntParam(final String paramKey) {
        return Optional.ofNullable(getParams().get(paramKey)).map(s -> Integer.valueOf(s));
    }

    @SuppressWarnings("unchecked")
    public <T> T getOptionalParam(final String paramKey, final T defaultValue) {
        return this.<T>getOptionalParam(paramKey).orElse(defaultValue);
    }

    @SuppressWarnings("unchecked")
    public Integer getOptionalIntParam(final String paramKey, final Integer defaultValue) {
        return this.getOptionalIntParam(paramKey).orElse(defaultValue);
    }

    public String getUri() {
        return uri;
    }

    public String getScheme() {
        return scheme;
    }

    public String getQuery() {
        return query;
    }

    public JsonObject getBody() {
        return body;
    }

    public static class Builder {

        private HttpMethod method;
        private String absoluteURI;
        private MultiMap headers;
        private String host;
        private MultiMap params;
        private String uri;
        private String scheme;
        private String query;
        private JsonObject body;

        public Builder(final RoutingContext routingContext) {
            final HttpServerRequest request = routingContext.request();

            io.vertx.rxjava.core.buffer.Buffer bodyBuffer = routingContext.getBody();
            if (bodyBuffer != null && bodyBuffer.length() > 0) {
                body = routingContext.getBodyAsJson();
            }

            method = request.method();
            absoluteURI = request.absoluteURI();
            headers = request.headers();
            host = request.host();
            params = request.params();
            uri = request.uri();
            scheme = request.scheme();
            query = request.query();
        }

        public Builder() {
        }

        public Builder withMethod(final HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder withAbsoluteURI(final String absoluteURI) {
            this.absoluteURI = absoluteURI;
            return this;
        }

        public Builder withHeaders(final MultiMap headers) {
            this.headers = headers;
            return this;
        }

        public Builder withHost(final String host) {
            this.host = host;
            return this;
        }

        public Builder withParams(final MultiMap params) {
            this.params = params;
            return this;
        }

        public Builder withUri(final String uri) {
            this.uri = uri;
            return this;
        }

        public Builder withScheme(final String scheme) {
            this.scheme = scheme;
            return this;
        }

        public Builder withQuery(final String query) {
            this.query = query;
            return this;
        }

        public Builder withBody(final JsonObject body) {
            this.body = body;
            return this;
        }

        public RequestInput build() {
            return new RequestInput(this);
        }
    }

    public static class Codec implements MessageCodec<RequestInput, RequestInput> {
        @Override
        public void encodeToWire(final Buffer buffer, final RequestInput requestInput) {
            throw new UnsupportedOperationException();
        }

        @Override
        public RequestInput decodeFromWire(final int pos, final Buffer buffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public RequestInput transform(final RequestInput requestInput) {
            return requestInput;
        }

        @Override
        public String name() {
            return this.getClass().getName();
        }

        @Override
        public byte systemCodecID() {
            return -1;
        }
    }
}
