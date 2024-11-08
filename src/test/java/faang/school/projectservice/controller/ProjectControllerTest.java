package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private ProjectDto projectDto;
    private UpdateProjectDto updateProjectDto;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
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
}