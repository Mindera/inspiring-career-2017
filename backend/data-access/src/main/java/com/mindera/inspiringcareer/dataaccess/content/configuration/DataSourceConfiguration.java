package com.mindera.inspiringcareer.dataaccess.content.configuration;

import com.mindera.inspiringcareer.utils.Loggable;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${application.properties}", ignoreResourceNotFound = true)
@EnableJpaRepositories(basePackages = "com.mindera.inspiringcareer.dataaccess.content.repository")
@ComponentScan("com.mindera.inspiringcareer.dataaccess.content.service")
public class DataSourceConfiguration implements Loggable {

    private static final String PROD = "prod";

    @Value("${dataSourceClassName}")
    private String dataSourceClassName;

    @Value("${datasource.databaseName}")
    private String databaseName;

    @Value("${datasource.serverName}")
    private String databaseServerName;

    @Value("${datasource.portNumber:5432}")
    private String databasePortNumber;

    @Value("${datasource.username}")
    private String username;

    @Value("${datasource.password}")
    private String password;

    @Value("${hikari.datasource.maxPool.size:15}")
    private int maxPoolSize;


    @Value("${hikari.datasource.connectionTimeout:10000}")
    private int connectionTimeout;

    @Value("${hikari.datasource.registerMbeans:true}")
    private boolean registerMbeans;

    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2ddl;

    @Value("${hibernate.show_sql}")
    private String showSQL;

    @Value("${hibernate.dialect}")
    private String dialect;

    /**
     * WARNING: This will clean you database. Never use this property in production
     */
    @Value("${qa.environment.forceCleanDb:false}")
    private boolean forceCleanDb;

    @Value("${ENV:DEV}")
    private String env;

    @Bean
    public DataSource dataSource() {
        final Properties props = new Properties();
        props.setProperty("dataSourceClassName", dataSourceClassName);
        props.setProperty("dataSource.databaseName", databaseName);
        props.setProperty("dataSource.serverName", databaseServerName);
        props.setProperty("dataSource.portNumber", databasePortNumber);
        props.setProperty("dataSource.user", username);
        props.setProperty("dataSource.password", password);
        props.setProperty("dataSource.databaseName", databaseName);

        final HikariConfig config = new HikariConfig(props);
        config.setDataSourceClassName(dataSourceClassName);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setConnectionTimeout(connectionTimeout);
        config.setRegisterMbeans(registerMbeans);
        return new HikariDataSource(config);
    }

    @Bean
    @Autowired
    public Flyway flyway(final DataSource dataSource) {
        final Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        return flyway;
    }

    @Bean
    @Autowired
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource, final Flyway flyway) {
        if (forceCleanDb && !PROD.equalsIgnoreCase(env)) {
            flyway.clean();
        }
        try {
            flyway.migrate();
        } catch (final Exception e) {
            logger().fatal("Flyway migration failed, doing a repair and retrying ...", e);
        }

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(Boolean.TRUE);
        vendorAdapter.setShowSql(Boolean.TRUE);

        final Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", hbm2ddl);
        jpaProperties.put("hibernate.show_sql", showSQL);
        jpaProperties.put("hibernate.dialect", dialect);

        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.mindera.inspiringcareer.dataaccess.content.domain");
        factory.setJpaProperties(jpaProperties);
        return factory;
    }

    @Bean
    @Autowired
    public PlatformTransactionManager transactionManager(final LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    void setHbm2ddl(final String hbm2ddl) {
        this.hbm2ddl = hbm2ddl;
    }

    String getShowSQL() {
        return showSQL;
    }

    void setShowSQL(final String showSQL) {
        this.showSQL = showSQL;
    }

    String getDialect() {
        return dialect;
    }

    void setDialect(final String dialect) {
        this.dialect = dialect;
    }

    boolean isForceCleanDb() {
        return forceCleanDb;
    }

    void setForceCleanDb(final boolean forceCleanDb) {
        this.forceCleanDb = forceCleanDb;
    }

    String getEnv() {
        return env;
    }

    void setEnv(final String env) {
        this.env = env;
    }
}
