package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void getProjectEntityByIdSuccess(){
        Mockito.lenient().when(projectRepository.getProjectById(Mockito.anyLong())).thenReturn(new Project());
        assertEquals(new Project(), projectService.getProjectEntityById(1L));
    }

    @Test
    void getProjectEntityByIdFail(){
        Mockito.lenient().when(projectRepository.getProjectById(1L)).thenThrow(new EntityNotFoundException("Project not found by id: %s".formatted(1L)));
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> projectService.getProjectEntityById(1L));
        assertEquals("Project not found by id: %s".formatted(1L), exception.getMessage());
    }
}