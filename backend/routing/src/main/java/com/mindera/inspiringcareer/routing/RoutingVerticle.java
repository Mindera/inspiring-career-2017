package com.mindera.inspiringcareer.routing;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mindera.inspiringcareer.utils.Loggable;
import com.mindera.inspiringcareer.utils.domain.RequestInput;
import com.mindera.inspiringcareer.utils.domain.RequestOutput;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.Route;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.CorsHandler;

class RoutingVerticle extends AbstractVerticle implements Loggable {

    private static final int DEFAULT_PORT = 8080;
    private static final boolean DEFAULT_CORS_CONFIG = false;

    private final JsonObject routingMap;
    private final Integer port;
    private final Boolean enableCors;
    private final String allowedOrigins;

    @Inject
    RoutingVerticle(@Named("config.app") final JsonObject appConfig,
                    @Named("routes") final JsonObject routingMap,
                    @Named("config.development.cors") final JsonObject corsConfig) {
        this.routingMap = routingMap;
        this.port = appConfig.getInteger("http.port", DEFAULT_PORT);
        this.enableCors = corsConfig.getBoolean("enable", DEFAULT_CORS_CONFIG);
        this.allowedOrigins = corsConfig.getString("allowedOrigins", "");
    }

    @Override
    public void start(final Future<Void> start) {
        final Router router = createRoutes();

        registerDefaultEventBusCodecs();

        final HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setPort(port)
                .setCompressionSupported(true);

        vertx.createHttpServer(httpServerOptions)
                .requestHandler(router::accept)
                .rxListen()
                .subscribe(res -> start.complete(), error -> {
                    logger().error("Failed to deploy routing verticle", error);
                    start.fail(error.getCause());
                });
    }

    private void registerDefaultEventBusCodecs() {
        final EventBus eventBus = this.getVertx().eventBus();

        eventBus.registerDefaultCodec(RequestInput.class, new RequestInput.Codec());
        eventBus.registerDefaultCodec(RequestOutput.class, new RequestOutput.Codec());
    }

    private Router createRoutes() {

        // Create the router object
        Router router = Router.router(vertx);

        if (enableCors) {
            router.route().handler(CorsHandler.create(allowedOrigins));
        }

        router.route().handler(BodyHandler.create()
                .setMergeFormAttributes(true));

        // Start application binding
        routingMap.forEach(entry -> registerRoute(router, entry.getKey(), (JsonObject) entry.getValue()));

        return router;
    }

    private Route registerRoute(final Router router, final String route, final JsonObject methods) {
        return router.route(route).handler(routingContext -> routeHandler(methods, routingContext));
    }

    private void routeHandler(final JsonObject methods, final RoutingContext routingContext) {
        logger().info("request {} {}", routingContext.request().method(), routingContext.request().uri());

        final HttpMethod method = routingContext.request().method();
        final String address = methods.getString(method.toString());

        if (HttpMethod.OPTIONS.equals(method)) {
            routingContext.response().setStatusCode(HttpResponseStatus.OK.code()).end();
        } else if (address == null) {
            routingContext.response().setStatusCode(HttpResponseStatus.METHOD_NOT_ALLOWED.code()).end();
        } else {
            final RequestInput requestInput = new RequestInput.Builder(routingContext).build();

            this.vertx.eventBus().<RequestOutput>rxSend(address, requestInput)
                    .map(Message::body)
                    .subscribe(
                            response -> reply(routingContext, response),
                            error -> routingContext.response()
                                    .setStatusCode(((ReplyException) error).failureCode())
                                    .setStatusMessage(error.getMessage())
                                    .end());
        }
    }

    private void reply(final RoutingContext routingContext, final RequestOutput response) {
        final HttpResponseStatus status = response.getStatusCode() == null
                ? HttpResponseStatus.OK
                : response.getStatusCode();

        logger().info("request " + routingContext.request().method() + " " + routingContext.request().uri() + " " + status.code());

        final HttpServerResponse httpServerResponse = routingContext.response()
                .setStatusCode(status.code());

        if (response.getBody() == null) {
            httpServerResponse.end();
        } else {
            httpServerResponse
                    .putHeader(HttpHeaders.CONTENT_TYPE.toString(), response.getContentType())
                    .end(response.getBody());
        }
    }
}
