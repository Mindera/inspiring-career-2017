package com.mindera.inspiringcareer.guice.binders;

import com.google.inject.AbstractModule;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class VertxBinder extends AbstractModule {
    private final Vertx vertx;

    public VertxBinder(final Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    protected void configure() {
        bind(Vertx.class)
                .toInstance(vertx);

        bind(EventBus.class)
                .toInstance(vertx.eventBus());
    }
}
