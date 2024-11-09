package faang.school.projectservice.service;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
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
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;

    @BeforeEach
    void setUp() {
        project = createTestProject();
    }

    @Test
    @DisplayName("Get project by id success")
    void testGetProjectByIdSuccess() {
        when(projectRepository.getProjectById(project.getId())).thenReturn(project);

        Project result = projectService.getProjectById(project.getId());

        assertNotNull(result);
        assertEquals(project, result);
        assertEquals("Project 1", result.getName());
    }

    @Test
    @DisplayName("Get project by id fail")
    void testGetProjectByIdFail() {
        when(projectRepository.getProjectById(project.getId())).
                thenThrow(new EntityNotFoundException(String.format("Project with id %s doesn't exist", project.getId())));

        Exception ex = assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(project.getId()));
        assertEquals("Project with id 1 doesn't exist", ex.getMessage());
    }

    private Project createTestProject() {
        return Project.builder()
                .id(1L)
                .name("Project 1")
                .build();
    }
}