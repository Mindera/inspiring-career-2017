package com.mindera.inspiringcareer.guice.binders;

import com.google.inject.AbstractModule;
import com.mindera.inspiringcareer.dataaccess.content.ApplicationContextSingleton;
import org.springframework.context.ApplicationContext;

public class ApplicationContextBinder extends AbstractModule {
    @Override
    protected void configure() {
        bind(ApplicationContext.class)
                .toInstance(ApplicationContextSingleton.getInstance());
    }
}
