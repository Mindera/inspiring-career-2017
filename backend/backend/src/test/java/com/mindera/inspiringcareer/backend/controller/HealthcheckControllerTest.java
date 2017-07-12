package com.mindera.inspiringcareer.backend.controller;

import com.mindera.inspiringcareer.backend.delegate.HealthcheckDelegate;
import io.vertx.ext.unit.TestContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HealthcheckControllerTest extends ControllerTestCase {

    @Mock
    private HealthcheckDelegate healthcheckDelegate;

    private HealthcheckController victim;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        victim = new HealthcheckController(this.healthcheckDelegate);
    }

    @Test
    public void testHealthcheck(final TestContext context) {
        testControllerConsumer(context, victim, healthcheckDelegate::get, "healthcheck");
    }
}
