package faang.school.projectservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.controller.ProjectController;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        projectDto = createTestProjectDto();
    }

    private ProjectDto createTestProjectDto() {
        ProjectDto dto = new ProjectDto();
        dto.setId(1L);
        dto.setName("Test Project");
        dto.setDescription("A sample project description");
        dto.setOwnerId(100L);
        dto.setStatus(ProjectStatus.CREATED);
        dto.setVisibility(ProjectVisibility.PUBLIC);
        dto.setCreatedAt(LocalDateTime.now());
        return dto;
    }

    private ResultActions performPostRequest(String url, ProjectDto projectDto) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectDto)));
    }

    private ResultActions performGetRequest(String url) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createProject_Success() throws Exception {
        when(projectService.createProject(any(ProjectDto.class))).thenReturn(projectDto);

        performPostRequest("/projects", projectDto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(projectDto.getName()))
                .andExpect(jsonPath("$.ownerId").value(projectDto.getOwnerId()));
    }

    @Test
    void updateProject_Success() throws Exception {
        when(projectService.updateProject(eq(1L), any(ProjectDto.class))).thenReturn(projectDto);

        performPostRequest("/projects/1", projectDto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectDto.getId()))
                .andExpect(jsonPath("$.name").value(projectDto.getName()));
    }

    @Test
    void findProjects_Success() throws Exception {
        when(projectService.findProjects("Test Project", ProjectStatus.CREATED, ProjectVisibility.PUBLIC, 1L))
                .thenReturn(List.of(projectDto));

        mockMvc.perform(get("/projects")
                        .param("name", "Test Project")
                        .param("status", "CREATED")
                        .param("visibility", "PUBLIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value(projectDto.getName()));
    }

    @Test
    void getAllProjects_Success() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of(projectDto));

        performGetRequest("/projects/all")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value(projectDto.getName()));
    }

    @Test
    void getProjectById_Success() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(projectDto);

        performGetRequest("/projects/1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectDto.getId()))
                .andExpect(jsonPath("$.name").value(projectDto.getName()));
    }

    @Test
    void createProject_ValidationError() throws Exception {
        ProjectDto invalidDto = new ProjectDto(); // empty to trigger validation error

        performPostRequest("/projects", invalidDto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists()); // Customize based on actual error response
    }

    @Test
    void updateProject_NotFound() throws Exception {
        when(projectService.updateProject(eq(1L), any(ProjectDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        performPostRequest("/projects/1", projectDto)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Project not found"));
    }

    @Test
    void getProjectById_NotFound() throws Exception {
        when(projectService.getProjectById(1L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        performGetRequest("/projects/1")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Project not found"));
    }
}
