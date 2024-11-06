package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {

    @Mock
    private ProjectJpaRepository projectRepository;

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
        when(projectRepository.existsByOwnerIdAndName(ownerId, projectName)).thenReturn(true);

        assertThrows(EntityNotFoundException.class,
                () -> projectValidator.validateUniqueProject(projectName, ownerId));
    }
}