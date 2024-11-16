package faang.school.projectservice.statusupdator;

import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CancelledStatusUpdateTest {

    @InjectMocks
    private CancelledStatusUpdate cancelledStatusUpdate;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private ProjectRepository projectRepository;

    private Project project;
    private UpdateSubProjectDto updateSubProjectDto;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setStatus(ProjectStatus.CREATED);

        updateSubProjectDto = new UpdateSubProjectDto();
        updateSubProjectDto.setStatus(ProjectStatus.CANCELLED);
    }

    @Test
    void testIsApplicableWhenStatusIsCancelled() {
        boolean result = cancelledStatusUpdate.isApplicable(updateSubProjectDto);
        assertTrue(result);
    }

    @Test
    void testIsApplicableWhenStatusIsNotCancelled() {
        updateSubProjectDto.setStatus(ProjectStatus.COMPLETED);
        boolean result = cancelledStatusUpdate.isApplicable(updateSubProjectDto);
        assertFalse(result);
    }

    @Test
    void testChangeStatusForProjectWithoutChildren() {
        when(projectValidator.hasChildrenProjects(project)).thenReturn(false);

        cancelledStatusUpdate.changeStatus(project);

        verify(projectRepository, times(1)).save(project);
        assertEquals(ProjectStatus.CANCELLED, project.getStatus());
    }

    @Test
    void testChangeStatusForProjectWithChildren() {
        Project childProject = new Project();
        childProject.setStatus(ProjectStatus.CREATED);
        project.setChildren(List.of(childProject));

        when(projectValidator.hasChildrenProjects(project)).thenReturn(true);
        when(projectValidator.hasChildrenProjects(childProject)).thenReturn(false);

        cancelledStatusUpdate.changeStatus(project);

        verify(projectRepository, times(2)).save(any(Project.class));
        assertEquals(ProjectStatus.CANCELLED, project.getStatus());
        assertEquals(ProjectStatus.CANCELLED, childProject.getStatus());
    }

    @Test
    void testChangeStatusWhenChildrenHaveStatusChanged() {
        Project childProject = new Project();
        childProject.setStatus(ProjectStatus.CREATED);
        project.setChildren(List.of(childProject));

        when(projectValidator.hasChildrenProjects(project)).thenReturn(true);
        when(projectValidator.hasChildrenProjects(childProject)).thenReturn(false);

        cancelledStatusUpdate.changeStatus(project);

        verify(projectRepository, times(2)).save(any(Project.class));
        assertEquals(ProjectStatus.CANCELLED, project.getStatus());
        assertEquals(ProjectStatus.CANCELLED, childProject.getStatus());
    }
}