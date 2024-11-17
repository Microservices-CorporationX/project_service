package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;

    @BeforeEach
    public void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
    }

    @Test
    @DisplayName("Проверка getProjectById - получили проект по id")
    public void testGetProjectById_getProject() {
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        Project result = projectService.getProject(1L);

        assertEquals(project.getId(), result.getId());
        assertEquals(project.getName(), result.getName());

        verify(projectRepository, times(1)).getProjectById(1L);
    }
}
