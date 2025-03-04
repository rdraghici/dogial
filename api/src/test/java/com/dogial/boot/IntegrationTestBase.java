package com.dogial.boot;

import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import io.micronaut.security.token.render.AccessRefreshToken;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTestBase implements TestPropertyProvider {

    public static final String TEST_EMAIL = "test@example.com";

    @Inject
    protected JwtTokenGenerator tokenGenerator;

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

        // JWT configuration for tests
        properties.put("micronaut.security.token.jwt.signatures.secret.generator.secret",
                "thisIsAMockedDogialsecret01234567890ABCDEFGHIJKLMNO-PQRST");

        return properties;
    }

    /**
     * Generate JWT token for test authentication
     * @param email User email
     * @param roles User roles
     * @return JWT token string
     */
    protected String generateJwtToken(String email, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", email);  // subject claim
        claims.put("roles", roles);
        claims.put("email", email);

        Optional<String> token = tokenGenerator.generateToken(claims);
        return token.orElse(null);
    }

    /**
     * Generate Authorization header with Bearer token
     * @param email User email
     * @param roles User roles
     * @return Authorization header value
     */
    protected String bearerAuth(String email, List<String> roles) {
        return "Bearer " + generateJwtToken(email, List.of("ROLE_USER"));
    }
}