package faang.school.projectservice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class AppContextTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13.3"))
                .withDatabaseName("testdb")
                .withUsername("user")
                .withPassword("password")
                .waitingFor(Wait.forListeningPort());
    }

    @BeforeAll
    public static void setUp() {
        postgresContainer.start();
    }

    @Test
    public void contextLoads() {
        assertNotNull(applicationContext);

        jdbcTemplate.execute("SELECT 1");

        assertTrue(postgresContainer.isRunning());

        String result = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
        assertNotNull(result);
    }

    @AfterAll
    public static void tearDown() {
        postgresContainer.stop();
    }
}
