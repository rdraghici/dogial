package com.dogial.boot;

import io.micronaut.test.support.TestPropertyProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.HashMap;
import java.util.Map;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTestBase implements TestPropertyProvider {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("datasources.default.url", postgres.getJdbcUrl());
        properties.put("datasources.default.username", postgres.getUsername());
        properties.put("datasources.default.password", postgres.getPassword());
        properties.put("datasources.default.driverClassName", "org.postgresql.Driver");

        // Flyway configuration for test database
        properties.put("flyway.datasources.default.enabled", "true");
        properties.put("flyway.datasources.default.clean-schema", "true");
        properties.put("flyway.datasources.default.locations", "classpath:db/migration");

        return properties;
    }
}