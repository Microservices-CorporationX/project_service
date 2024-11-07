package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
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

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .build();
    }

    @Test
    void testCreateProjectSuccess() {
        when(projectService.createProject(projectDto)).thenReturn(projectDto);

        ResponseEntity<ProjectDto> response = projectController.createProject(projectDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(projectDto, response.getBody());
        verify(projectService, times(1)).createProject(projectDto);
    }

    @Test
    void testUpdateProjectDescription() {
        when(projectService.updateProjectDescription(projectDto)).thenReturn(projectDto);

        ResponseEntity<ProjectDto> response = projectController.updateProjectDescription(projectDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDto, response.getBody());
        verify(projectService, times(1)).updateProjectDescription(projectDto);
    }

    @Test
    void testUpdateProjectStatus() {
        when(projectService.updateProjectStatus(projectDto)).thenReturn(projectDto);

        ResponseEntity<ProjectDto> response = projectController.updateProjectStatus(projectDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDto, response.getBody());
        verify(projectService, times(1)).updateProjectStatus(projectDto);
    }

    @Test
    void testUpdateProjectVisibility() {
        when(projectService.updateProjectVisibility(projectDto)).thenReturn(projectDto);

        ResponseEntity<ProjectDto> response = projectController.updateProjectVisibility(projectDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDto, response.getBody());
        verify(projectService, times(1)).updateProjectVisibility(projectDto);
    }
}