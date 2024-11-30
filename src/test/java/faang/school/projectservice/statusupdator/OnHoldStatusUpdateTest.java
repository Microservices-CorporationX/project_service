package faang.school.projectservice.statusupdator;

import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OnHoldStatusUpdateTest {
    private ProjectValidator projectValidator;
    private ProjectRepository projectRepository;
    private OnHoldStatusUpdate onHoldStatusUpdate;

    @BeforeEach
    void setUp() {
        projectValidator = mock(ProjectValidator.class);
        projectRepository = mock(ProjectRepository.class);
        onHoldStatusUpdate = new OnHoldStatusUpdate(projectValidator, projectRepository);
    }

    @Test
    @DisplayName("Is applicable returns true for ON_HOLD status")
    void testIsApplicableReturnsTrueForOnHoldStatus() {
        UpdateSubProjectDto dto = new UpdateSubProjectDto();
        dto.setStatus(ProjectStatus.ON_HOLD);

        boolean result = onHoldStatusUpdate.isApplicable(dto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Is applicable returns false for other statuses")
    void testIsApplicableReturnsFalseForOtherStatuses() {
        UpdateSubProjectDto dto = new UpdateSubProjectDto();
        dto.setStatus(ProjectStatus.CREATED);

        boolean result = onHoldStatusUpdate.isApplicable(dto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Change status sets project status to ON_HOLD")
    void testChangeStatusSetsProjectStatusToOnHold() {
        Project project = new Project();
        project.setStatus(ProjectStatus.CREATED);

        doNothing().when(projectValidator).validateProjectStatusValidToHold(project);
        when(projectValidator.hasChildrenProjects(project)).thenReturn(false);

        onHoldStatusUpdate.changeStatus(project);

        verify(projectValidator).validateProjectStatusValidToHold(project);
        verify(projectRepository).save(project);
        assertEquals(ProjectStatus.ON_HOLD, project.getStatus());
    }

    @Test
    @DisplayName("Change status updates children projects recursively")
    void testChangeStatusUpdatesChildrenProjectsRecursively() {
        Project parentProject = new Project();
        parentProject.setStatus(ProjectStatus.CREATED);

        Project child1 = new Project();
        child1.setStatus(ProjectStatus.CREATED);

        Project child2 = new Project();
        child2.setStatus(ProjectStatus.CREATED);

        List<Project> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        parentProject.setChildren(children);

        doNothing().when(projectValidator).validateProjectStatusValidToHold(any(Project.class));
        when(projectValidator.hasChildrenProjects(parentProject)).thenReturn(true);
        when(projectValidator.hasChildrenProjects(child1)).thenReturn(false);
        when(projectValidator.hasChildrenProjects(child2)).thenReturn(false);

        onHoldStatusUpdate.changeStatus(parentProject);

        assertEquals(ProjectStatus.ON_HOLD, parentProject.getStatus());
        assertEquals(ProjectStatus.ON_HOLD, child1.getStatus());
        assertEquals(ProjectStatus.ON_HOLD, child2.getStatus());

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository, times(3)).save(captor.capture());
        List<Project> savedProjects = captor.getAllValues();
        assertTrue(savedProjects.contains(parentProject));
        assertTrue(savedProjects.contains(child1));
        assertTrue(savedProjects.contains(child2));
    }
}