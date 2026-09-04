package com.ideiasmidias;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Boots the full Spring application context against a real PostgreSQL instance and
 * runs the Flyway migrations, verifying that the JPA entities validate against the
 * migrated schema.
 *
 * <p>This is an integration test (suffix {@code IT}) so it is executed by the
 * Failsafe plugin during {@code mvn verify}, not by the unit-test phase
 * {@code mvn test}. It needs a database, so CI provides one as a service container.
 */
@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
class ApplicationContextIT {

    @Test
    void contextLoads() {
        // Fails if the context cannot start (bad wiring, migration/entity mismatch, etc.).
    }
}
