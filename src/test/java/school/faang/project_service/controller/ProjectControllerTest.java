package school.faang.project_service.controller;

import faang.school.projectservice.controller.ProjectController;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    @Test
    void getProject_WhenProjectExists_ReturnsProjectDto() {
        long projectId = 1L;
        ProjectDto expectedProjectDto = new ProjectDto(projectId, "Test Project");
        when(projectService.getProject(projectId)).thenReturn(expectedProjectDto);
        ProjectDto result = projectController.getProject(projectId);

        assertEquals(expectedProjectDto, result);
    }

    @Test
    void getProject_WhenProjectDoesNotExist_ThrowsException() {
        long projectId = 999L;
        String errorMessage = "Project with ID " + projectId + " not found";
        when(projectService.getProject(projectId)).thenThrow(new RuntimeException(errorMessage));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> projectController.getProject(projectId)
        );
        assertEquals(errorMessage, exception.getMessage());
    }
}