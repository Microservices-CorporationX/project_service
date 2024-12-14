package faang.school.projectservice.controller.project;

import com.redis.testcontainers.RedisContainer;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ProjectControllerIT {

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @Value("${spring.data.redis.channels.projects_view_channel.name}")
    private String channelName;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ProjectController projectController;

    @Autowired
    private UserContext userContext;


    @Test
    void testViewProject_Positive() {
        try (Jedis jedis = new Jedis(REDIS_CONTAINER.getHost(), REDIS_CONTAINER.getMappedPort(6379))) {
            MyPubSub myPubSub = new MyPubSub();

            Thread subscriberThread = new Thread(() -> jedis.subscribe(myPubSub, channelName));
            subscriberThread.start();

            long projectId = 1L;
            userContext.setUserId(5L);
            ProjectResponseDto dto = projectController.viewProject(projectId);

            assertEquals(1, dto.getOwnerId());
            assertFalse(myPubSub.receivedMessage.isBlank());
            System.out.println(myPubSub.receivedMessage);
        }
    }

    @Test
    void testViewProject_userIsOwnerProject_NegativePublished() throws Exception {
        long projectId = 1L;
        long userId = 1L;
        mockMvc.perform(get("/api/v1/projects/view/{projectId}", projectId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testViewProject_notValidRequest_NegativePublished() throws Exception {
        long projectId = -1L;
        long userId = 1L;
        mockMvc.perform(get("/api/v1/projects/view/{projectId}", projectId)
                        .header("x-user-id", userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testViewProject_projectNotFound_NegativePublished() throws Exception {
        long projectId = 10L;
        long userId = 5L;
        mockMvc.perform(get("/api/v1/projects/view/{projectId}", projectId)
                        .header("x-user-id", userId))
                .andExpect(status().isNotFound());
    }

    @Getter
    private static class MyPubSub extends JedisPubSub {

        private String receivedMessage;

        @Override
        public void onMessage(String channel, String message) {
            receivedMessage = message;
        }

    }

    @DynamicPropertySource
    static void start(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
