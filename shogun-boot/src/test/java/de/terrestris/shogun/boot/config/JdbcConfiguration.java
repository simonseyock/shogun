package de.terrestris.shogun.boot.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.AfterClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration(proxyBeanMethods = false)
@PropertySource("jdbc.properties")
@EnableTransactionManagement
public class JdbcConfiguration {

    @Autowired
    private Environment env;

    private PostgreSQLContainer postgreSQLContainer;

    private static final Set<HikariDataSource> datasourcesForCleanup = new HashSet();

    @Bean
    public PostgreSQLContainer postgreSQLContainer() {
        if (postgreSQLContainer != null) {
            return postgreSQLContainer;
        }
        DockerImageName postgis = DockerImageName.parse("docker.terrestris.de/postgis/postgis:11-3.0").asCompatibleSubstituteFor("postgres");
        postgreSQLContainer = new PostgreSQLContainer(postgis);
        postgreSQLContainer.start();
        return postgreSQLContainer;
    }

    @Bean
    public DataSource dataSource(final JdbcDatabaseContainer databaseContainer) {
        HikariConfig hikariConfig = new HikariConfig();
        String jdbcUrl = String.format("%s&currentSchema=shogun", databaseContainer.getJdbcUrl());
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(databaseContainer.getUsername());
        hikariConfig.setPassword(databaseContainer.getPassword());
        hikariConfig.setDriverClassName(databaseContainer.getDriverClassName());

        final HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        datasourcesForCleanup.add(dataSource);

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean result = new LocalContainerEntityManagerFactoryBean();

        result.setDataSource(dataSource(postgreSQLContainer()));
        result.setPackagesToScan(env.getProperty("shogun.model.packages"));
        result.setJpaVendorAdapter(jpaVendorAdapter());

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        jpaProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        jpaProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        jpaProperties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        jpaProperties.put("hibernate.default_schema", env.getProperty("hibernate.default_schema"));
        jpaProperties.put("hibernate.integration.envers.enabled", false);

        result.setJpaPropertyMap(jpaProperties);

        return result;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @AfterClass
    public static void cleanup() {
        datasourcesForCleanup.forEach(HikariDataSource::close);
    }

}
