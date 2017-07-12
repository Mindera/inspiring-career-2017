package com.mindera.inspiringcareer.guice.binders;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.vertx.core.json.JsonObject;

public class VertxConfigBinder extends AbstractModule {
    private static final String CONFIG_ROOT_PATH = "config";
    private static final String ROUTES_ROOT_PATH = "routes";

    private final JsonObject config;
    private final JsonObject routes;

    public VertxConfigBinder(final JsonObject config,
                             final JsonObject routes) {
        this.config = config;
        this.routes = routes;
    }

    private void bind(final JsonObject configuration, final String path) {
        bind(JsonObject.class)
                .annotatedWith(Names.named(path))
                .toInstance(configuration);

        configuration
                .stream()
                .filter(entry -> entry.getValue() instanceof JsonObject)
                .forEach(entry -> bind((JsonObject) entry.getValue(), String.format("%s.%s", path, entry.getKey())));
    }

    @Override
    protected void configure() {
        bind(config, CONFIG_ROOT_PATH);

        bind(JsonObject.class)
                .annotatedWith(Names.named(ROUTES_ROOT_PATH))
                .toInstance(routes);
    }
}
