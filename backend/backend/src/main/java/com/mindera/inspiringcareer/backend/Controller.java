package com.mindera.inspiringcareer.backend;

import com.google.common.collect.Lists;
import com.mindera.inspiringcareer.utils.Loggable;
import com.mindera.inspiringcareer.utils.domain.RequestInput;
import com.mindera.inspiringcareer.utils.domain.RequestOutput;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import rx.Single;
import rx.functions.Action1;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Controller extends AbstractVerticle implements Loggable {

    private static final String ERROR_MESSAGE = "Failed to handle message.";

    private List<MessageConsumer<?>> consumers;

    public Controller() {
        this.consumers = Lists.newArrayList();
    }

    @Override
    public void start() {
        //Called at the start of the verticle
    }

    @Override
    public final void start(final Future<Void> startFuture) {
        try {
            start();
            logger().info("Deploying controller {}", this.getClass().getName());
            startFuture.complete();
        } catch (Exception e) {
            logger().error("Failed to deploy controller {}", e, this.getClass().getName());
            startFuture.fail(e);
        }
    }

    @Override
    public void stop() {
        unregisterConsumers();
    }

    @Override
    public void stop(final Future<Void> stopFuture) {
        stop();
        stopFuture.complete();
    }

    protected void consumer(final String address, final BiFunction<RequestInput, RequestOutput.Builder, Single<RequestOutput>> handler) {
        consumers.add(vertx.eventBus().consumer(address, messageHandler(handler)));
    }

    protected void consumer(final String address, final Function<RequestOutput.Builder, Single<RequestOutput>> handler) {
        consumers.add(vertx.eventBus().consumer(address, messageHandler(handler)));
    }

    protected void consumerWorker(final String address, final BiFunction<RequestInput, RequestOutput.Builder, Single<RequestOutput>> handler) {
        consumers.add(vertx.eventBus().consumer(address, messageHandlerWorker(handler)));
    }

    protected void consumerWorker(final String address, final Function<RequestOutput.Builder, Single<RequestOutput>> handler) {
        consumers.add(vertx.eventBus().consumer(address, messageHandlerWorker(handler)));
    }

    protected void unregisterConsumers() {
        consumers.forEach(MessageConsumer::unregister);
    }

    private Handler<Message<RequestInput>> messageHandlerWorker(final BiFunction<RequestInput, RequestOutput.Builder, Single<RequestOutput>> handler) {
        return message -> {
            final RequestInput body = message.body();

            executeHandlerBlocking(handler, body)
                    .subscribe(handleResponse(message), handleError(message));
        };
    }

    private Handler<Message<RequestInput>> messageHandlerWorker(final Function<RequestOutput.Builder, Single<RequestOutput>> handler) {
        return message -> executeHandlerBlocking(handler)
                .subscribe(handleResponse(message), handleError(message));
    }

    private Handler<Message<RequestInput>> messageHandler(final BiFunction<RequestInput, RequestOutput.Builder, Single<RequestOutput>> handler) {
        return message -> {
            final RequestInput body = message.body();

            handler.apply(body, new RequestOutput.Builder())
                    .subscribe(handleResponse(message), handleError(message));
        };
    }

    private Handler<Message<RequestInput>> messageHandler(final Function<RequestOutput.Builder, Single<RequestOutput>> handler) {
        return message -> handler.apply(new RequestOutput.Builder())
                .subscribe(handleResponse(message), handleError(message));
    }

    private Single<RequestOutput> executeHandlerBlocking(final BiFunction<RequestInput, RequestOutput.Builder, Single<RequestOutput>> handler,
                                                         final RequestInput body) {
        return vertx.rxExecuteBlocking(blocking -> handler.apply(body, new RequestOutput.Builder())
                .subscribe(blocking::complete, blocking::fail), false);
    }

    private Single<RequestOutput> executeHandlerBlocking(final Function<RequestOutput.Builder, Single<RequestOutput>> handler) {
        return vertx.rxExecuteBlocking(blocking -> handler.apply(new RequestOutput.Builder())
                .subscribe(blocking::complete, blocking::fail), false);
    }

    private Action1<RequestOutput> handleResponse(final Message<RequestInput> message) {
        return response -> {
            if (response.succeeded()) {
                message.reply(response);
            } else {
                message.fail(response.getStatusCode().code(), response.getMessage());
            }
        };
    }

    private <T> Action1<Throwable> handleError(final Message<T> message) {
        return error -> {
            logger().error(ERROR_MESSAGE, error);
            message.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), ERROR_MESSAGE + " " + error.getMessage());
        };
    }
}
