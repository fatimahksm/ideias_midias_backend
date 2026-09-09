package com.ideiasmidias;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Boots the whole application context — the smoke test that catches a broken
 * bean wiring or a Flyway migration that no longer applies.
 *
 * <p>It needs a real Postgres, so it only runs when one is pointed at through
 * {@code TEST_DB_URL} (see {@code application-test.properties}). Without that
 * variable it is skipped rather than failed, so {@code mvn test} stays green on
 * a laptop while CI, which does provide a database, still runs it.
 */
@SpringBootTest
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(
        named = "TEST_DB_URL",
        matches = ".+",
        disabledReason = "No TEST_DB_URL configured; skipping the context smoke test."
)
class IdeiasmidiasApplicationTests {

    @Test
    void contextLoads() {
    }

}
