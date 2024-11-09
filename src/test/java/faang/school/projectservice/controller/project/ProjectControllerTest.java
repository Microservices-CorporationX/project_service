package faang.school.projectservice.controller.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectCreateReq;
import faang.school.projectservice.dto.project.ProjectFiltersReq;
import faang.school.projectservice.dto.project.ProjectPatchReq;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.utilities.UrlUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("testcontainers")
public class ProjectControllerTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    static {
        postgres.withDatabaseName("test_db")
                .withUsername("user")
                .withPassword("password");
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    @Sql(scripts = "/deleteProject.sql" ,executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createProjectSuccessTest() throws Exception {
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectCreateReq("Project", "Description", ProjectVisibility.PUBLIC, 1L))))
                .andExpect(status().isOk());
    }

    @Test
    void createProjectWithEmptyNameFailTest() throws Exception {
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectCreateReq(null, "Description", ProjectVisibility.PUBLIC, 1L))))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("must not be blank"));
                });
    }

    @Test
    void createProjectWithNullDescriptionFailTest() throws Exception {
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectCreateReq("Project", null, ProjectVisibility.PUBLIC, 1L))))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("must not be blank"));
                });
    }

    @Test
    void createProjectWithNullStatusFailTest() throws Exception {
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectCreateReq("Project", "Description", null, 1L))))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("must not be null"));
                });
    }

    @Test
    void createProjectWithNegativeOwnerIdFailTest() throws Exception {
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectCreateReq("Project", "Description", ProjectVisibility.PUBLIC, -1L))))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("must be greater than or equal to 1"));
                });
    }

    @Test
    void createProjectWithAlreadyExistentNameForOwnerIdFailTest() throws Exception {
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectCreateReq("Test1", "Description", ProjectVisibility.PUBLIC, 2L))))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("Project with name: Test1 already exists for this owner id: 2"));
                });
    }

    @Test
    void patchProjectSuccessTest() throws Exception {
        mockMvc.perform(patch(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectPatchReq(6L, "Description", ProjectStatus.CANCELLED))))
                .andExpect(status().isOk());
    }

    @Test
    void patchProjectWithNegativeIdFailTest() throws Exception {
        mockMvc.perform(patch(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectPatchReq(-1L, "Description", ProjectStatus.ON_HOLD))))
                .andExpect(status().is4xxClientError())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("must be greater than or equal to 1"));
                });
    }

    @Test
    void patchProjectForNonExistentProjectFailTest() throws Exception {
        mockMvc.perform(patch(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectPatchReq(100L, "Description", ProjectStatus.ON_HOLD))))
                .andExpect(status().isNotFound())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("Project not found by id: 100"));
                });
    }

    @Test
    void findProjectsWithFullFiltersSuccessTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS + UrlUtils.FILTER)
                        .header("x-user-id", 3L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectFiltersReq("Test3", ProjectStatus.IN_PROGRESS))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(3)))
                .andExpect(jsonPath("$.[0].name", is("Test3")))
                .andExpect(jsonPath("$.[0].description", is("Test3")))
                .andExpect(jsonPath("$.[0].createdAt", is("2024-11-08T22:38:20.307877")))
                .andExpect(jsonPath("$.[0].status", is(ProjectStatus.IN_PROGRESS.name())))
                .andExpect(jsonPath("$.[0].visibility", is(ProjectVisibility.PRIVATE.name())));
    }

    @Test
    void findProjectsWithOnlyUserIdFilterSuccessTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS + UrlUtils.FILTER)
                        .header("x-user-id", 3L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectFiltersReq(null, null))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is("Test1")))
                .andExpect(jsonPath("$.[0].visibility", is(ProjectVisibility.PUBLIC.name())))
                .andExpect(jsonPath("$.[1].id", is(2)))
                .andExpect(jsonPath("$.[1].name", is("Test2")))
                .andExpect(jsonPath("$.[1].visibility", is(ProjectVisibility.PUBLIC.name())))
                .andExpect(jsonPath("$.[2].id", is(3)))
                .andExpect(jsonPath("$.[2].name", is("Test3")))
                .andExpect(jsonPath("$.[2].visibility", is(ProjectVisibility.PRIVATE.name())))
                .andExpect(jsonPath("$.[3].id", is(4)))
                .andExpect(jsonPath("$.[3].name", is("Test4")))
                .andExpect(jsonPath("$.[3].visibility", is(ProjectVisibility.PUBLIC.name())));
    }

    @Test
    void findProjectsWithUserIdAndNameFilterSuccessTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS + UrlUtils.FILTER)
                        .header("x-user-id", 3L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectFiltersReq("Test3", null))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(3)))
                .andExpect(jsonPath("$.[0].name", is("Test3")))
                .andExpect(jsonPath("$.[0].visibility", is(ProjectVisibility.PRIVATE.name())));
    }

    @Test
    void findProjectsWithUserIdAndStatusFilterSuccessTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS + UrlUtils.FILTER)
                        .header("x-user-id", 5L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectFiltersReq(null, ProjectStatus.ON_HOLD))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(5)))
                .andExpect(jsonPath("$.[0].name", is("Test5")))
                .andExpect(jsonPath("$.[0].visibility", is(ProjectVisibility.PRIVATE.name())))
                .andExpect(jsonPath("$.[0].status", is(ProjectStatus.ON_HOLD.name())));
    }

    @Test
    void findProjectsWithFiltersEmptyResultSuccessTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS + UrlUtils.FILTER)
                        .header("x-user-id", 3L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProjectFiltersReq("Test7", ProjectStatus.CREATED))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findProjectsSuccessTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$.[0].name", is("Test1")))
                .andExpect(jsonPath("$.[1].name", is("Test2")))
                .andExpect(jsonPath("$.[2].name", is("Test3")))
                .andExpect(jsonPath("$.[3].name", is("Test4")))
                .andExpect(jsonPath("$.[4].name", is("Test5")))
                .andExpect(jsonPath("$.[5].name", is("Test6")));
    }

    @Test
    void findProjectByIdSuccessTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS + "/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Test3")))
                .andExpect(jsonPath("$.description", is("Test3")))
                .andExpect(jsonPath("$.ownerId", is(3)))
                .andExpect(jsonPath("$.createdAt", is("2024-11-08T22:38:20.307877")))
                .andExpect(jsonPath("$.status", is(ProjectStatus.IN_PROGRESS.name())))
                .andExpect(jsonPath("$.visibility", is(ProjectVisibility.PRIVATE.name())));
    }

    @Test
    void findProjectByIdFailTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS + "/300"))
                .andExpect(status().isNotFound())
                .andDo(mvcResult -> {
                    String content = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
                    assertTrue(content.contains("Project not found by id: 300"));
                });
    }
}
