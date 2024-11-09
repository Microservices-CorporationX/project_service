package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {
    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectValidator projectValidator;
    private Long projectId = 1L;
    private Project project;

    @BeforeEach
    public void setUp() {
        project = new Project();
        project.setId(projectId);
    }
    @Test
    public void testIsOpenProjectWhenStatusCreated() {
        project.setStatus(ProjectStatus.CREATED);
        when(projectService.getProjectById(projectId)).thenReturn(project);

        assertTrue(projectValidator.isOpenProject(projectId));
    }

    @Test
    public void testIsOpenProjectWhenStatusInProgress() {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        when(projectService.getProjectById(projectId)).thenReturn(project);

        assertTrue(projectValidator.isOpenProject(projectId));
    }

    @Test
    public void testIsOpenProjectWhenStatusCompleted() {
        project.setStatus(ProjectStatus.COMPLETED);
        when(projectService.getProjectById(projectId)).thenReturn(project);

        assertFalse(projectValidator.isOpenProject(projectId));
    }

    @Test
    public void testIsOpenProjectWhenStatusCancelled() {
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectService.getProjectById(projectId)).thenReturn(project);

        assertFalse(projectValidator.isOpenProject(projectId));
    }
}
