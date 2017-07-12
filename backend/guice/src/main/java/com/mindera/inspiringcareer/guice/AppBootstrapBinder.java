package com.mindera.inspiringcareer.guice;

import com.google.inject.AbstractModule;
import com.mindera.inspiringcareer.guice.binders.ApplicationContextBinder;
import com.mindera.inspiringcareer.guice.binders.BeanBinder;

public class AppBootstrapBinder extends AbstractModule {

    public AppBootstrapBinder() {
    }

    @Override
    protected void configure() {
        install(new ApplicationContextBinder());
        install(new BeanBinder());
    }
}
