package com.mindera.inspiringcareer.guice.binders;

import com.google.inject.AbstractModule;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;

public class RxVertxBinder extends AbstractModule {
    private final Vertx vertx;

    public RxVertxBinder(final Vertx vertx) {
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
