package faang.school.projectservice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class AppContextTest {
    @Autowired
    private ProjectServiceApplication projectServiceApplication;

    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13.3"))
                .withCreateContainerCmdModifier(cmd -> cmd.withName("testDb"))
                .withUsername("user")
                .withPassword("password");
    }

    @BeforeAll
    public static void setUp() {
        postgresContainer.start();
    }

    @Test
    public void contextLoads() {
        assertNotNull(projectServiceApplication);
    }

    @AfterAll
    public static void tearDown() {
        postgresContainer.stop();
    }
}
