package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ProjectService projectService;

    @Test
    public void testProjectIsOpenWhenStatusIsCreated() {
        Long projectId = 1L;
        Project project = new Project();
        project.setStatus(ProjectStatus.CREATED);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        boolean result = projectService.projectIsOpen(projectId);

        assertTrue("Project should be open when status is CREATED", result);
    }

    @Test
    public void testProjectIsOpenWhenStatusInProgress() {
        Long projectId = 1L;
        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        boolean result = projectService.projectIsOpen(projectId);

        assertTrue("Project should be open when status is In_Progress", result);
    }

    @Test
    public void testProjectIsOpenWhenStatusIsCompleted() {
        Long projectId = 1L;
        Project project = new Project();
        project.setStatus(ProjectStatus.COMPLETED);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        boolean result = projectService.projectIsOpen(projectId);

        assertFalse("Project should not be open when status is Completed", result);
    }

    @Test
    public void testProjectIsOpenWhenStatusIsClosed() {
        Long projectId = 1L;
        Project project = new Project();
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        boolean result = projectService.projectIsOpen(projectId);

        assertFalse("Project should not be open when status is Cancelled", result);
    }

    @Test
    public void testfindProjectByIdWhenProjectExist() {
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        Project foundProject = projectService.getProjectById(projectId);

        assertNotNull(foundProject);
        assertEquals(projectId, foundProject.getId());
        verify(projectRepository, times(1)).getProjectById(projectId);
    }

    @Test
    public void testfindProjectByIdWhenProjectNotExist() {
        Long projectId = 1L;
        when(projectRepository.getProjectById(projectId)).thenThrow(new EntityNotFoundException
                ("Project not found by id: " + projectId));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> projectService.getProjectById(projectId));

        assertEquals("Project not found by id: " + projectId, exception.getMessage());
        verify(projectRepository, times(1)).getProjectById(projectId);
    }
}
