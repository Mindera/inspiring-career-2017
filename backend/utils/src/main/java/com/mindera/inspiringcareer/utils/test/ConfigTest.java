package com.mindera.inspiringcareer.utils.test;

import com.mindera.inspiringcareer.utils.JsonUtils;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.net.ServerSocket;

public class ConfigTest {

    private static final String APP = "app";
    private static final String HTTP_PORT = "http.port";
    private static final String GUICE_BINDER = "guice_binder";

    private final JsonObject config;

    public ConfigTest() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        final int port = socket.getLocalPort();
        socket.close();

        final JsonObject appConfig = new JsonObject().put(HTTP_PORT, port);
        config = new JsonObject()
                .put(APP, appConfig)
                .put(GUICE_BINDER, "com.mindera.content.guice.BootstrapBinder")
                .put("development", new JsonObject()
                        .put("cors", JsonUtils.emptyJsonObject()));
    }

    public JsonObject getConfig() {
        return this.config;
    }

}
