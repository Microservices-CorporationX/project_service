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
class CompletedStatusUpdateTest {

    @InjectMocks
    private CompletedStatusUpdate completedStatusUpdate;

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
        updateSubProjectDto.setStatus(ProjectStatus.COMPLETED);
    }

    @Test
    @DisplayName("Is applicable")
    void testIsApplicableWhenStatusIsCompleted() {
        boolean result = completedStatusUpdate.isApplicable(updateSubProjectDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Is not applicable")
    void testIsApplicableWhenStatusIsNotCompleted() {
        updateSubProjectDto.setStatus(ProjectStatus.CANCELLED);
        boolean result = completedStatusUpdate.isApplicable(updateSubProjectDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Change status when project is valid to complete")
    void testChangeStatusWhenProjectIsValidToComplete() {
        doNothing().when(projectValidator).validateProjectIsValidToComplete(project);

        completedStatusUpdate.changeStatus(project);

        verify(projectRepository, times(1)).save(project);
        assertEquals(ProjectStatus.COMPLETED, project.getStatus());
    }
}