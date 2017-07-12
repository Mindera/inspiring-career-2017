package com.mindera.inspiringcareer.backend.delegate;

import com.mindera.inspiringcareer.utils.domain.RequestOutput;
import io.vertx.core.json.JsonObject;
import rx.Single;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class HealthcheckDelegate {
    public Single<RequestOutput> get(final RequestOutput.Builder response) {
        return Single.just(response
                .withStatusCode(OK)
                .withBody(new JsonObject().put("status", "ok"))
                .build());
    }
}
