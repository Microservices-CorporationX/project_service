package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private Long ownerId;
    private ProjectDto projectDto;
    private UpdateProjectDto updateProjectDto;
    private ProjectFilterDto filterDto;

    @BeforeEach
    void setUp() {
        ownerId = 1L;
        projectDto = ProjectDto.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(ownerId)
                .status(ProjectStatus.CREATED)
                .build();

        filterDto = ProjectFilterDto.builder()
                .name("Test project")
                .status(ProjectStatus.CREATED)
                .build();

        updateProjectDto = UpdateProjectDto.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @Test
    void testCreateProjectSuccessful() {
        when(projectService.createProject(projectDto)).thenReturn(projectDto);

        ResponseEntity<ProjectDto> response = projectController.createProject(projectDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(projectDto, response.getBody());
        verify(projectService, times(1)).createProject(projectDto);
    }

    @Test
    void testUpdateProjectSuccessful() {
        when(projectService.updateProject(updateProjectDto)).thenReturn(updateProjectDto);

        ResponseEntity<UpdateProjectDto> response = projectController.updateProjectDescription(updateProjectDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updateProjectDto, response.getBody());
        verify(projectService, times(1)).updateProject(updateProjectDto);
    }

    @Test
    void testGetProjectsByFilterSuccessful() {
        when(projectService.getProjectsByFilter(filterDto, ownerId)).thenReturn(List.of(projectDto));

        ResponseEntity<List<ProjectDto>> response = projectController.getProjectsByFilter(filterDto, ownerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDto, Objects.requireNonNull(response.getBody()).get(0));
        verify(projectService, times(1)).getProjectsByFilter(filterDto, ownerId);
    }

    @Test
    void testGetAllProjectSuccess() {
        when(projectService.getAllProjectsForUser(ownerId)).thenReturn(List.of(projectDto));

        ResponseEntity<List<ProjectDto>> response = projectController.getAllProjects(ownerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDto, Objects.requireNonNull(response.getBody()).get(0));
        verify(projectService, times(1)).getAllProjectsForUser(ownerId);
    }

    @Test
    void testGetProjectByIdSuccess() {
        projectDto.setId(1L);
        when(projectService.getAccessibleProjectsById(projectDto.getId(), ownerId)).thenReturn(projectDto);

        ResponseEntity<ProjectDto> response = projectController.getProjectById(projectDto.getId(), ownerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDto, response.getBody());
        verify(projectService, times(1)).getAccessibleProjectsById(projectDto.getId(), ownerId);
    }

    @Test
    void getProjectsByIdsWhenProjectsExistShouldReturnProjectDtos() {
        List<Long> ids = List.of(1L, 2L);

        when(projectService.findAllById(ids)).thenReturn(List.of(projectDto));

        List<ProjectDto> response = projectController.getProjectsByIds(ids).getBody();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(projectDto.getId(), response.get(0).getId());
        assertEquals(projectDto.getName(), response.get(0).getName());

        verify(projectService, times(1)).findAllById(ids);
    }

    @Test
    void getProjectsByIdsWhenNoProjectsExistShouldReturnEmptyList() {
        List<Long> ids = List.of(1L, 2L);

        when(projectService.findAllById(ids)).thenReturn(List.of());

        List<ProjectDto> response = projectController.getProjectsByIds(ids).getBody();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(projectService, times(1)).findAllById(ids);
    }

    @Test
    void getProjectWhenProjectExistsShouldReturnProjectDto() {
        long projectId = 1L;

        when(projectService.findById(projectId)).thenReturn(projectDto);

        ProjectDto response = projectController.getProject(projectId).getBody();

        assertNotNull(response);
        assertEquals(projectDto.getId(), response.getId());
        assertEquals(projectDto.getName(), response.getName());

        verify(projectService, times(1)).findById(projectId);
    }

    @Test
    void getProjectWhenProjectDoesNotExistShouldThrowEntityNotFoundException() {
        long projectId = 999L;

        when(projectService.findById(projectId)).thenThrow(new EntityNotFoundException("Project not found"));

        assertThrows(EntityNotFoundException.class, () -> projectController.getProject(projectId));

        verify(projectService, times(1)).findById(projectId);
    }
}