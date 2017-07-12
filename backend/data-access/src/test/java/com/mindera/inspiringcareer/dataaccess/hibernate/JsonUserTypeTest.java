package com.mindera.inspiringcareer.dataaccess.hibernate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JsonUserTypeTest {

    private JsonUserType victim;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        victim = new JsonUserType();
    }

    @Test
    public void shouldBeFalse() {
        final String stringA = "A";
        final String stringB = "B";

        assertFalse(victim.equals(stringA, stringB));
    }

    @Test
    public void shouldBeEqualStrings() {
        final String stringA = "A";
        final String stringB = "A";

        assertTrue(victim.equals(stringA, stringB));
    }

    @Test
    public void shouldBeEqualNullObjects() {
        assertTrue(victim.equals(null, null));
    }
}
