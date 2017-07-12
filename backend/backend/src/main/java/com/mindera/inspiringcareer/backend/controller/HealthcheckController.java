package com.mindera.inspiringcareer.backend.controller;

import com.google.inject.Inject;
import com.mindera.inspiringcareer.backend.Controller;
import com.mindera.inspiringcareer.backend.delegate.HealthcheckDelegate;

public class HealthcheckController extends Controller {

    private final HealthcheckDelegate healthcheckDelegate;

    @Inject
    public HealthcheckController(final HealthcheckDelegate healthcheckDelegate) {
        this.healthcheckDelegate = healthcheckDelegate;
    }

    @Override
    public void start() {
        consumer("healthcheck", healthcheckDelegate::get);
    }
}
