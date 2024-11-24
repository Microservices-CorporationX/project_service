package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectCreateResponseDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateResponseDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exception.ProjectVisibilityException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigInteger;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SubProjectControllerTest {

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private SubProjectController subProjectController;

    private CreateProjectDto createProjectDto;
    private ProjectCreateResponseDto projectCreateResponseDto;
    private ProjectUpdateResponseDto projectUpdateResponseDto;
    private ProjectDto projectDto;
    private UpdateSubProjectDto updateSubProjectDto;
    private ProjectFilterDto projectFilterDto;
    private Long postId = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subProjectController).build();
        objectMapper = new ObjectMapper();
        createProjectDto = mockCreateProjectDto();
        projectCreateResponseDto = mockProjectResponseDto();
        projectUpdateResponseDto = mockProjectUpdateResponseDto();
        updateSubProjectDto = mockUpdateSubProjectDto();
        projectDto = mockProjectDto();
        projectFilterDto = mockProjectFilterDto();
    }

    @Test
    @DisplayName("Create subproject success")
    void testCreateSubProject() throws Exception {
        when(projectService.createSubProject(postId, createProjectDto)).thenReturn(projectCreateResponseDto);

        mockMvc.perform(post("/api/v1/subprojects/1/subprojects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createProjectDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.description").value("John description"))
                .andExpect(jsonPath("$.visibility").value("PUBLIC"));

        verify(projectService).createSubProject(postId, createProjectDto);
    }

    @Test
    @DisplayName("Create subproject fail")
    void testCreateSubProjectFail() {
        when(projectService.createSubProject(postId, createProjectDto)).thenThrow(ProjectVisibilityException.class);

        assertThrows(ProjectVisibilityException.class, () -> subProjectController.createSubProject(postId, createProjectDto));
    }

    @Test
    @DisplayName("Update subproject success")
    void testUpdateSubProject() throws Exception {
        when(projectService.updateSubProject(updateSubProjectDto)).thenReturn(projectUpdateResponseDto);

        mockMvc.perform(put("/api/v1/subprojects")
                .content(objectMapper.writeValueAsString(updateSubProjectDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.visibility").value("PUBLIC"));

        verify(projectService).updateSubProject(updateSubProjectDto);
    }

    @Test
    @DisplayName("Update subproject fail")
    void testUpdateSubProjectFail() {
        when(projectService.updateSubProject(updateSubProjectDto)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> subProjectController.updateSubProject(updateSubProjectDto));
    }

    @Test
    @DisplayName("Filter subprojects success")
    void testFilterSubProjects() throws Exception {
        List<ProjectDto> projectDtos = List.of(projectDto, projectDto, projectDto);
        when(projectService.filterSubProjects(postId, projectFilterDto)).thenReturn(projectDtos);

        mockMvc.perform(get("/api/v1/subprojects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectFilterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("John description"))
                .andExpect(jsonPath("$[2].name").value("John"))
                .andExpect(jsonPath("$", hasSize(3)));

        verify(projectService).filterSubProjects(postId, projectFilterDto);
    }

    @Test
    @DisplayName("Filter subprojects fail")
    void testFilterSubProjectsFail() {
        when(projectService.filterSubProjects(postId, projectFilterDto)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> subProjectController.filterSubProjects(postId, projectFilterDto));
    }

    private ProjectDto mockProjectDto() {
        return ProjectDto.builder()
                .name("John")
                .description("John description")
                .ownerId(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private CreateProjectDto mockCreateProjectDto() {
        return CreateProjectDto.builder()
                .name("John")
                .description("John description")
                .storageSize(BigInteger.ONE)
                .maxStorageSize(BigInteger.TEN)
                .ownerId(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private ProjectCreateResponseDto mockProjectResponseDto() {
        return ProjectCreateResponseDto.builder()
                .name("John")
                .description("John description")
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private ProjectUpdateResponseDto mockProjectUpdateResponseDto() {
        return ProjectUpdateResponseDto.builder()
                .name("John")
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private UpdateSubProjectDto mockUpdateSubProjectDto() {
        return UpdateSubProjectDto.builder()
                .id(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private ProjectFilterDto mockProjectFilterDto() {
        return ProjectFilterDto.builder()
                .status(ProjectStatus.CREATED)
                .build();
    }
}