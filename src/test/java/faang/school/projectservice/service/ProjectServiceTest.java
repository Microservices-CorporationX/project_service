package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    public void testFindProjectById() {
        // Arrange
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        // Act
        Project result = projectService.findProjectById(1L);

        // Assert
        assertEquals(project, result);
    }
}