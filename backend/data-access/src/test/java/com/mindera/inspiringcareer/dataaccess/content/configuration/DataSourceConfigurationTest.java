package com.mindera.inspiringcareer.dataaccess.content.configuration;

import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class DataSourceConfigurationTest {

    @Mock
    private Flyway flyway;
    @Mock
    private DataSource dataSource;

    private DataSourceConfiguration dataSourceConfiguration;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dataSourceConfiguration = new DataSourceConfiguration();
    }

    @Test
    public void testEntityManagerFactoryCleanDB() throws Exception {
        dataSourceConfiguration.setForceCleanDb(true);
        dataSourceConfiguration.setEnv("dev");
        dataSourceConfiguration.setHbm2ddl("");
        dataSourceConfiguration.setShowSQL("");
        dataSourceConfiguration.setDialect("");

        dataSourceConfiguration.entityManagerFactory(dataSource, flyway);

        verify(flyway).clean();
        verify(flyway).migrate();
    }

    @Test
    public void testEntityManagerFactory() throws Exception {
        dataSourceConfiguration.setForceCleanDb(false);
        dataSourceConfiguration.setEnv("dev");
        dataSourceConfiguration.setHbm2ddl("");
        dataSourceConfiguration.setShowSQL("");
        dataSourceConfiguration.setDialect("");

        dataSourceConfiguration.entityManagerFactory(dataSource, flyway);

        verify(flyway, never()).clean();
        verify(flyway).migrate();
    }

    @Test
    public void testEntityManagerFactoryProd() throws Exception {
        dataSourceConfiguration.setForceCleanDb(true);
        dataSourceConfiguration.setEnv("prod");
        dataSourceConfiguration.setHbm2ddl("");
        dataSourceConfiguration.setShowSQL("");
        dataSourceConfiguration.setDialect("");

        dataSourceConfiguration.entityManagerFactory(dataSource, flyway);

        verify(flyway, never()).clean();
        verify(flyway).migrate();
    }
}
