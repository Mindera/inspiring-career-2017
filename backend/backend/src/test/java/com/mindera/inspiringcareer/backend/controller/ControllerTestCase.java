package com.mindera.inspiringcareer.backend.controller;

import com.mindera.inspiringcareer.utils.domain.RequestInput;
import com.mindera.inspiringcareer.utils.domain.RequestOutput;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import rx.Single;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(VertxUnitRunner.class)
public abstract class ControllerTestCase {

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    private EventBus eventBus;

    @Before
    public void internalSetUp() {
        this.eventBus = EventBus.newInstance(this.rule.vertx().eventBus());
        rule.vertx().eventBus().registerDefaultCodec(RequestInput.class, new RequestInput.Codec());
        rule.vertx().eventBus().registerDefaultCodec(RequestOutput.class, new RequestOutput.Codec());
    }

    private void mockConsumerHandler(final BiFunction<RequestInput, RequestOutput.Builder, Single<RequestOutput>> method) {
        final RequestOutput requestOutput = new RequestOutput.Builder()
                .withBody("stuff")
                .build();

        when(method.apply(any(RequestInput.class), any(RequestOutput.Builder.class)))
                .thenReturn(Single.just(requestOutput));
    }

    private void mockConsumerHandler(final Function<RequestOutput.Builder, Single<RequestOutput>> method) {
        final RequestOutput requestOutput = new RequestOutput.Builder()
                .withBody("stuff")
                .build();

        when(method.apply(any(RequestOutput.Builder.class)))
                .thenReturn(Single.just(requestOutput));
    }

    void testControllerConsumer(final TestContext context,
                                final AbstractVerticle verticle,
                                final BiFunction<RequestInput, RequestOutput.Builder, Single<RequestOutput>> method,
                                final String address) {
        final Async async = context.async(3);
        mockConsumerHandler(method);
        deployVerticleAndSendMessage(context, verticle, address, async);
    }

    void testControllerConsumer(final TestContext context,
                                final AbstractVerticle verticle,
                                final Function<RequestOutput.Builder, Single<RequestOutput>> method,
                                final String address) {
        final Async async = context.async(3);
        mockConsumerHandler(method);
        deployVerticleAndSendMessage(context, verticle, address, async);
    }

    private void deployVerticleAndSendMessage(final TestContext context, final AbstractVerticle verticle, final String address, final Async async) {
        rule.vertx().deployVerticle(verticle, deploymentId -> {
            if (deploymentId.succeeded()) {
                async.countDown();
                sendMessage(context, address, async, deploymentId.result());
            } else {
                context.fail();
            }
        });
    }

    private void sendMessage(final TestContext context, final String address, final Async async, final String deploymentId) {
        final RequestInput message = new RequestInput.Builder().build();
        this.eventBus.<RequestOutput>rxSend(address, message)
                .map(Message::body)
                .subscribe(result -> {
                    context.assertEquals("stuff", result.getBody());
                    async.countDown();
                    rule.vertx().undeploy(deploymentId, undeployResult -> async.complete());
                }, context::fail);
    }
}
