package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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

    private Project project;
    private Long projectId = 1L;

    @BeforeEach
    public void setUp() {
        project = new Project();
        project.setId(projectId);
        project.setName("Test Project");
    }

    @Test
    public void testGetProjectByIdWhenProjectExist() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        Project foundProject = projectService.getProjectById(projectId);

        assertNotNull(foundProject);
        assertEquals(projectId, foundProject.getId());
        verify(projectRepository, times(1)).getProjectById(projectId);
    }

    @Test
    public void testGetProjectByIdWhenProjectNotExist() {
        when(projectRepository.getProjectById(projectId)).thenThrow(new EntityNotFoundException("Project not found by id: " + projectId));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> projectService.getProjectById(projectId));

        assertEquals("Project not found by id: " + projectId, exception.getMessage());
        verify(projectRepository, times(1)).getProjectById(projectId);
    }

    @Test
    public void testFindAllByIdWhenProjectsExist() {
        List<Long> ids = List.of(1L, 2L, 3L);
        Project project1 = new Project();
        project1.setId(1L);
        Project project2 = new Project();
        project2.setId(2L);
        Project project3 = new Project();
        project3.setId(3L);
        when(projectRepository.findAllByIds(ids)).thenReturn(List.of(project1, project2, project3));

        List<Project> result = projectService.findAllById(ids);

        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(3L, result.get(2).getId());
    }

    @Test
    public void testFindAllByIdWhenNoProjectsFound() {
        List<Long> ids = List.of(1L, 2L, 3L);
        when(projectRepository.findAllByIds(ids)).thenReturn(List.of());

        List<Project> result = projectService.findAllById(ids);
        assertEquals(0, result.size());
    }
}

