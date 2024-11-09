package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EntityNotFoundException;
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
class ProjectValidatorTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectValidator projectValidator;

    private Long ownerId;
    private String projectName;

    @BeforeEach
    void setUp() {
        ownerId = 1L;
        projectName = "testName";
    }

    @Test
    void testValidateUniqueProjectFailed() {
        when(projectRepository.existsByOwnerUserIdAndName(ownerId, projectName)).thenReturn(true);

        assertThrows(EntityNotFoundException.class,
                () -> projectValidator.validateUniqueProject(projectName, ownerId));
    }

    @Test
    @DisplayName("Check project exists")
    void testValidateProjectExistsById() {
        Long projectId = 1L;
        when(projectRepository.existsById(projectId)).thenReturn(true);

        assertDoesNotThrow(() -> projectValidator.validateProjectExistsById(projectId));

        verify(projectRepository, times(1)).existsById(projectId);
    }

    @Test
    @DisplayName("Check project doesn't exist")
    void testValidateProjectInVacancyNotExists() {
        Long projectId = 1L;
        when(projectRepository.existsById(projectId)).thenReturn(false);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> projectValidator.validateProjectExistsById(projectId));
        assertEquals("Project with id 1 doesn't exist", ex.getMessage());

        verify(projectRepository, times(1)).existsById(projectId);
    }
}