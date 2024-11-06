package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;

    @BeforeEach
    void setUp() {
        project = createTestProject();
    }

    @Test
    @DisplayName("Get project by id")
    void getProjectById() {
        when(projectRepository.getProjectById(project.getId())).thenReturn(project);

        Project result = projectService.getProjectById(project.getId());
        
        assertNotNull(result);
        assertEquals(project, result);
        assertEquals("Project 1", result.getName());
    }

    @Test
    @DisplayName("Check project exists by id")
    void checkProjectExistsById() {
        when(projectRepository.existsById(project.getId())).thenReturn(true);

        boolean result = projectService.checkProjectExistsById(project.getId());

        verify(projectRepository, times(1)).existsById(anyLong());
        assertTrue(result);
    }

    @Test
    @DisplayName("Check project exists by invalid id")
    void checkProjectExistsByInvalidId() {
        when(projectRepository.existsById(project.getId())).thenReturn(false);

        boolean result = projectService.checkProjectExistsById(project.getId());

        verify(projectRepository, times(1)).existsById(anyLong());
        assertFalse(result);
    }

    private Project createTestProject() {
        return Project.builder()
                .id(1L)
                .name("Project 1")
                .build();
    }
}