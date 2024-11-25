package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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
        project.setStatus(ProjectStatus.COMPLETED);
    }

    @Test
    public void testGetProjectById() {
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        assertEquals(project, projectService.getProjectById(1L));
    }

    @Test
    public void testGetProjectByIdNotFound() {
        when(projectRepository.getProjectById(project.getId())).thenReturn(null);

        assertNull(projectService.getProjectById(project.getId()));
    }

    @Test
    public void testIsProjectComplete_Completed() {
        when(projectRepository.getProjectById(project.getId())).thenReturn(project);

        assertTrue(projectService.isProjectComplete(project.getId()));
    }

    @Test
    public void testIsProjectComplete_NotCompleted() {
        when(projectRepository.getProjectById(project.getId())).thenReturn(project);
        project.setStatus(ProjectStatus.IN_PROGRESS);

        assertFalse(projectService.isProjectComplete(project.getId()));
    }

}
