package com.mindera.inspiringcareer.utils.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.net.MediaType;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import rx.Single;

import java.util.Optional;

public class RequestOutput {
    private final HttpResponseStatus statusCode;
    private final String body;
    private final String contentType;
    private final boolean success;
    private final String message;

    public RequestOutput(final HttpResponseStatus statusCode,
                         final String body,
                         final String contentType,
                         final boolean success,
                         final String message) {
        this.statusCode = statusCode;
        this.body = body;
        this.contentType = contentType;
        this.success = success;
        this.message = message;
    }

    public RequestOutput(final Builder builder) {
        this.statusCode = builder.statusCode;
        this.body = builder.body;
        this.contentType = builder.contentType;
        this.success = builder.success;
        this.message = builder.message;
    }

    public HttpResponseStatus getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public String getContentType() {
        return Optional.ofNullable(contentType)
                .orElse(MediaType.JSON_UTF_8.toString());
    }

    public boolean succeeded() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public static class Builder {
        private HttpResponseStatus statusCode;
        private String body;
        private String contentType;
        private boolean success;
        private String message;

        public Builder() {
            this.success = true;
        }

        public Builder withStatusCode(final HttpResponseStatus statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder withBody(final Object body) throws JsonProcessingException {
            this.body = Json.mapper.writeValueAsString(body);
            return this;
        }

        public Builder withBody(final JsonArray body) {
            this.body = body.encode();
            return this;
        }

        public Builder withBody(final JsonObject body) {
            this.body = body.encode();
            return this;
        }

        public Builder withBody(final String body) {
            this.body = body;
            return this;
        }

        /**
         * This method will serialize the body to a Json. It is wrapped in an Observable to catch the exception and be
         * usable within Observables in a cleaner way
         *
         * @param body the body of the response
         * @return a reference of itself wrapped in an observable, a failed observable in case of a failed serialization
         */
        public Single<Builder> withBodyEncode(final Object body) {
            try {
                this.body = Json.mapper.writeValueAsString(body);
            } catch (JsonProcessingException e) {
                return Single.error(e);
            }
            return Single.just(this);
        }

        public Builder withContentType(final String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder withSuccess(final boolean success) {
            this.success = success;
            return this;
        }

        public Builder succeed() {
            this.success = true;
            return this;
        }

        public Builder fail() {
            this.success = false;
            return this;
        }

        public Builder withMessage(final String message) {
            this.message = message;
            return this;
        }

        public RequestOutput build() {
            return new RequestOutput(this);
        }
    }

    public static class Codec implements MessageCodec<RequestOutput, RequestOutput> {
        @Override
        public void encodeToWire(final Buffer buffer, final RequestOutput requestInput) {
            throw new UnsupportedOperationException();
        }

        @Override
        public RequestOutput decodeFromWire(final int pos, final Buffer buffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public RequestOutput transform(final RequestOutput requestOutput) {
            return requestOutput;
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
