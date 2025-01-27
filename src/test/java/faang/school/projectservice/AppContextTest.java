package faang.school.projectservice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AppContextTest {
    @Autowired
    private ProjectServiceApplication projectServiceApplication;

    @Autowired
    private DataSource dataSource;

    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13.3"))
                .withCreateContainerCmdModifier(cmd -> cmd.withName("testDb"))
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
        assertNotNull(projectServiceApplication);

        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDown() {
        postgresContainer.stop();
    }
}
