package faang.school.projectservice;

import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest(properties = "spring.profiles.active=test")
@Testcontainers
public class ProjectServiceApplicationTest {

    private static final Logger log = LoggerFactory.getLogger(ProjectServiceApplicationTest.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis/redis-stack:latest")
            .withExposedPorts(6379);

    static {
        System.out.println("Redis image name: " + redisContainer.getDockerImageName());
    }

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:13.3")
                    .withDatabaseName("postgres")
                    .withUsername("user")
                    .withPassword("password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);

        log.info("Using Redis image: {}", redisContainer.getDockerImageName());
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
        registry.add("testcontainers.image.redis", () -> "redis/redis-stack:latest");

        registry.add("services.payment-service.host", () -> "http://localhost");
        registry.add("services.payment-service.port", () -> 9080);

        registry.add("services.user-service.host", () -> "http://localhost");
        registry.add("services.user-service.port", () -> 8080);
    }

    @Test
    public void shouldLoadApplicationContext() {
        Assertions.assertNotNull(applicationContext, "Application context should load successfully");
    }

    @Test
    public void shouldFindSpecificBeansInContext() {
        boolean beanExists = applicationContext.containsBean("vacancyRepository");
        log.info("Bean found: {}", beanExists);
        Assertions.assertTrue(beanExists, "VacancyRepository bean should exist in the application context");

        VacancyRepository myRepository = applicationContext.getBean(VacancyRepository.class);
        log.info("VacancyRepository bean: {}", myRepository);
        Assertions.assertNotNull(myRepository, "VacancyRepository bean should not be null");

        Object vacancyRepositoryBean = applicationContext.getBean("vacancyRepository");
        log.info("VacancyRepository bean class: {}", vacancyRepositoryBean.getClass().getName());
        Assertions.assertTrue(
                VacancyRepository.class.isAssignableFrom(vacancyRepositoryBean.getClass()),
                "Bean class should implement VacancyRepository"
        );

        TeamMemberRepository teamMemberRepositoryBean = applicationContext.getBean(TeamMemberRepository.class);
        log.info("TeamMemberRepository bean: {}", teamMemberRepositoryBean);
        Assertions.assertNotNull(teamMemberRepositoryBean, "TeamMemberRepository bean should not be null");
    }
}
