package com.mindera.inspiringcareer.backend.delegate;

import com.mindera.inspiringcareer.utils.domain.RequestOutput;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@RunWith(VertxUnitRunner.class)
public class HealthcheckDelegateTest {

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    private HealthcheckDelegate victim;

    @Before
    public void setUp() throws Exception {
        victim = new HealthcheckDelegate();
    }

    @Test
    public void testHealthcheck(final TestContext context) {
        final Async async = context.async();

        victim.get(new RequestOutput.Builder())
                .subscribe(result -> {
                    context.assertEquals(OK, result.getStatusCode());
                    context.assertEquals(new JsonObject().put("status", "ok").encode(), result.getBody());
                    async.complete();
                }, error -> context.fail());
    }
}
