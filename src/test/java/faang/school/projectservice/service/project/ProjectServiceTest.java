package faang.school.projectservice.service.project;

import faang.school.projectservice.jpa.ProjectJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectJpaRepository projectRepository;

    @InjectMocks
    ProjectService projectService;

    @Test
    void isProjectExistsTrueTest() {
        when(projectRepository.existsById(1L)).thenReturn(true);

        boolean existsById = projectService.isProjectExists(1L);

        assertTrue(existsById);
        verify(projectRepository, times(1)).existsById(1L);
    }

    @Test
    void isProjectExistsFalseTest() {
        when(projectRepository.existsById(1L)).thenReturn(false);

        boolean existsById = projectService.isProjectExists(1L);

        assertFalse(existsById);
        verify(projectRepository, times(1)).existsById(1L);
    }
}