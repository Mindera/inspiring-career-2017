package com.mindera.inspiringcareer.routing;

import com.mindera.inspiringcareer.utils.JsonUtils;
import com.mindera.inspiringcareer.utils.domain.RequestOutput;
import com.mindera.inspiringcareer.utils.test.ConfigTest;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;

@RunWith(VertxUnitRunner.class)
public class RoutingVerticleTest {

    private Vertx vertx;

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    private int port;

    @Before
    public void setUp() throws IOException {
        vertx = rule.vertx();
        MockitoAnnotations.initMocks(this);

        final JsonObject routes = new JsonObject()
                .put("/test", new JsonObject()
                        .put("GET", "testGet")
                        .put("HEAD", "testOption"))
                .put("/bo/test", new JsonObject()
                        .put("authentication", true)
                        .put("GET", "apiTestGet"))
                .put("/bo/fail/eventBus", new JsonObject()
                        .put("GET", "apiTestFailEventBus"))
                .put("/bo/accepted", new JsonObject()
                        .put("GET", "apiTestAccepted"));

        final EventBus eventBus = vertx.eventBus();
        final RequestOutput.Builder builder = new RequestOutput.Builder();

        eventBus.consumer("testGet", message -> message.reply(builder.withBody("GET OK").build()));
        eventBus.consumer("testOption", message -> message.reply(builder.withBody("OPTION OK").build()));
        eventBus.consumer("apiTestGet", message -> message.reply(builder.withBody("GET OK").build()));
        eventBus.consumer("apiTestFailEventBus", message -> message.fail(403, "this failed."));
        eventBus.consumer("apiTestAccepted", message -> message.reply(builder.withBody((String) null).withStatusCode(ACCEPTED).build()));

        JsonObject config = new ConfigTest().getConfig();

        port = config.getJsonObject("app").getInteger("http.port");

        vertx.deployVerticle(new RoutingVerticle(config.getJsonObject("app"), routes,
                JsonUtils.emptyJsonObject()));
    }

    @Test
    public void testRoutes(final TestContext context) {
        final Async async = context.async(6);

        vertx.createHttpClient()
                .getNow(port, "localhost", "/test", response -> response.bodyHandler(body -> {
                    context.assertEquals("GET OK", body.toString());
                    async.countDown();
                }))
                .optionsNow(port, "localhost", "/test", response -> {
                    context.assertEquals(200, response.statusCode());
                    async.countDown();
                })
                .headNow(port, "localhost", "/bo/test", response -> {
                    context.assertEquals(405, response.statusCode());
                    async.countDown();
                })
                .getNow(port, "localhost", "/bo/test", response -> response.bodyHandler(body -> {
                    context.assertEquals("GET OK", body.toString());
                    async.countDown();
                }))
                .getNow(port, "localhost", "/bo/fail/eventBus", response -> {
                    context.assertEquals(403, response.statusCode());
                    context.assertEquals("this failed.", response.statusMessage());
                    async.countDown();
                })
                .getNow(port, "localhost", "/bo/accepted", response -> {
                    context.assertEquals(202, response.statusCode());
                    async.complete();
                })
                .close();
    }
}
