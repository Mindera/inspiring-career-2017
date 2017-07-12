package com.mindera.inspiringcareer.backend;

import com.mindera.inspiringcareer.utils.domain.RequestInput;
import com.mindera.inspiringcareer.utils.domain.RequestOutput;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import rx.Single;

@RunWith(VertxUnitRunner.class)
public class ControllerTest {

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    private EventBus eventBus;

    @Mock
    private RequestInput requestInput;

    private Controller victim;

    @Before
    public void setUp() throws Exception {
        final io.vertx.core.Vertx vertx = rule.vertx();
        Vertx rxVertx = Vertx.newInstance(vertx);

        this.eventBus = rxVertx.eventBus();
        this.victim = new Controller();

        vertx.eventBus().registerDefaultCodec(RequestInput.class, new RequestInput.Codec());
        vertx.eventBus().registerDefaultCodec(RequestOutput.class, new RequestOutput.Codec());

        vertx.deployVerticle(victim);
    }

    @Test
    public void testBiFunctionConsumerError(final TestContext context) {
        final Async async = context.async(2);

        victim.consumer("address", (requestInput, requestOutput) -> {
            async.countDown();
            return Single.error(new Throwable("stuff"));
        });

        this.eventBus.<RequestOutput>rxSend("address", requestInput)
                .subscribe(message -> context.fail(), error -> {
                    context.assertTrue(error instanceof ReplyException);
                    context.assertEquals(500, ((ReplyException) error).failureCode());
                    context.assertEquals("Failed to handle message. stuff", error.getMessage());
                    async.complete();
                });
    }

    @Test
    public void testBiFunctionConsumerFailure(final TestContext context) {
        final Async async = context.async(2);

        victim.consumer("address", (requestInput, requestOutput) -> {
            requestOutput
                    .withStatusCode(HttpResponseStatus.BAD_REQUEST)
                    .withMessage("stuff")
                    .fail();
            async.countDown();
            return Single.just(requestOutput.build());
        });

        this.eventBus.<RequestOutput>rxSend("address", requestInput)
                .subscribe(message -> context.fail(), error -> {
                    context.assertTrue(error instanceof ReplyException);
                    context.assertEquals(400, ((ReplyException) error).failureCode());
                    context.assertEquals("stuff", error.getMessage());
                    async.complete();
                });
    }

    @Test
    public void testBiFunctionConsumer(final TestContext context) {
        final Async async = context.async(2);

        victim.consumer("address", (requestInput, requestOutput) -> {
            requestOutput
                    .withStatusCode(HttpResponseStatus.OK)
                    .withBody("stuff")
                    .withSuccess(true);
            async.countDown();
            return Single.just(requestOutput.build());
        });

        this.eventBus.<RequestOutput>rxSend("address", requestInput)
                .subscribe(message -> {
                    final RequestOutput body = message.body();
                    context.assertEquals("stuff", body.getBody());
                    async.complete();
                });
    }

    @Test
    public void testFunctionConsumer(final TestContext context) {
        final Async async = context.async(2);

        victim.consumer("address", requestOutput -> {
            requestOutput
                    .withStatusCode(HttpResponseStatus.OK)
                    .withBody("stuff")
                    .withSuccess(true);
            async.countDown();
            return Single.just(requestOutput.build());
        });

        this.eventBus.<RequestOutput>rxSend("address", null)
                .subscribe(message -> {
                    final RequestOutput body = message.body();
                    context.assertEquals("stuff", body.getBody());
                    async.complete();
                });
    }

    @Test
    public void testBiFunctionConsumerWorker(final TestContext context) {
        final Async async = context.async(2);

        victim.consumerWorker("address", (requestInput, requestOutput) -> {
            requestOutput
                    .withStatusCode(HttpResponseStatus.OK)
                    .withBody("stuff")
                    .withSuccess(true);
            async.countDown();
            return Single.just(requestOutput.build());
        });

        this.eventBus.<RequestOutput>rxSend("address", requestInput)
                .subscribe(message -> {
                    final RequestOutput body = message.body();
                    context.assertEquals("stuff", body.getBody());
                    async.complete();
                });
    }

    @Test
    public void testFunctionConsumerWorker(final TestContext context) {
        final Async async = context.async(2);

        victim.consumerWorker("address", requestOutput -> {
            requestOutput
                    .withStatusCode(HttpResponseStatus.OK)
                    .withBody("stuff")
                    .withSuccess(true);
            async.countDown();
            return Single.just(requestOutput.build());
        });

        this.eventBus.<RequestOutput>rxSend("address", null)
                .subscribe(message -> {
                    final RequestOutput body = message.body();
                    context.assertEquals("stuff", body.getBody());
                    async.complete();
                });
    }
}
