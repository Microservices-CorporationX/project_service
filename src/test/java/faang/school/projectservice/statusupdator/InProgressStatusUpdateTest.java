package faang.school.projectservice.statusupdator;

import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InProgressStatusUpdateTest {

    @InjectMocks
    private InProgressStatusUpdate inProgressStatusUpdate;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private ProjectRepository projectRepository;

    private Project project;
    private Project parentProject;
    private UpdateSubProjectDto updateSubProjectDto;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setStatus(ProjectStatus.CREATED);

        parentProject = new Project();
        parentProject.setStatus(ProjectStatus.CREATED);

        project.setParentProject(parentProject);

        updateSubProjectDto = new UpdateSubProjectDto();
        updateSubProjectDto.setStatus(ProjectStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Is applicable")
    void testIsApplicableWhenStatusIsInProgress() {
        boolean result = inProgressStatusUpdate.isApplicable(updateSubProjectDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Is not applicable")
    void testIsApplicableWhenStatusIsNotInProgress() {
        updateSubProjectDto.setStatus(ProjectStatus.CANCELLED);
        boolean result = inProgressStatusUpdate.isApplicable(updateSubProjectDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Change status when project has no parent")
    void testChangeStatusWhenProjectHasNoParent() {
        project.setParentProject(null);

        inProgressStatusUpdate.changeStatus(project);

        verify(projectRepository, times(1)).save(project);
        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());
    }

    @Test
    @DisplayName("Change status when project has parent")
    void testChangeStatusWhenProjectHasParent() {
        when(projectValidator.hasParentProject(project)).thenReturn(true);

        inProgressStatusUpdate.changeStatus(project);

        verify(projectRepository, times(2)).save(any(Project.class));
        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());
        assertEquals(ProjectStatus.IN_PROGRESS, parentProject.getStatus());
    }
}