package com.mindera.inspiringcareer.dataaccess.content;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class ApplicationContextSingleton {

    private static volatile ApplicationContext instance;

    private ApplicationContextSingleton() {
    }

    public static synchronized ApplicationContext getInstance() {
        if (instance == null) {
            // TODO - uncomment if uses database persistence
            //instance = new AnnotationConfigApplicationContext(DataSourceConfiguration.class);
            instance = new AnnotationConfigApplicationContext();
        }

        return instance;
    }
}
