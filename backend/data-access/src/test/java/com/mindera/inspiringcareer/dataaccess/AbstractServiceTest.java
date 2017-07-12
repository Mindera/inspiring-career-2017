package com.mindera.inspiringcareer.dataaccess;

import com.mindera.inspiringcareer.utils.JsonUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.when;

public class AbstractServiceTest {

    private AbstractService victim;

    @Mock
    private ResultSet resultSet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        victim = Mockito.mock(AbstractService.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void shouldReturnEmptyObjectNullRows() {
        when(resultSet.getRows())
                .thenReturn(null);

        assertEquals(victim.getSingle(resultSet), JsonUtils.emptyJsonObject());
    }

    @Test
    public void shouldReturnEmptyObjectEmptyRows() {
        when(resultSet.getRows())
                .thenReturn(new ArrayList<>());

        assertEquals(victim.getSingle(resultSet), JsonUtils.emptyJsonObject());
    }

    @Test
    public void shouldReturnFirstRow() {
        final List<JsonObject> resultRows = new ArrayList<>();
        final JsonObject row = new JsonObject().put("row", "0");

        resultRows.add(row);
        resultRows.add(new JsonObject().put("row", "1"));

        when(resultSet.getRows())
                .thenReturn(resultRows);

        final JsonObject result = victim.getSingle(resultSet);

        assertNotNull(result);
        assertEquals(result, row);
    }
}
