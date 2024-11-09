package faang.school.projectservice.service;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;
    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectDto projectDto;

    private final long projectId = 1L;

    @BeforeEach
    void setUp() {
        project = Project
                .builder()
                .id(projectId)
                .name("Project 1")
                .description("Description 1")
                .build();

        projectDto = ProjectDto
                .builder()
                .name("Project 1")
                .description("Description 1")
                .build();
    }

    @Test
    void testGetByIdThrowsException() {
        when(projectRepository.getProjectById(projectId)).thenThrow(new EntityNotFoundException(
                String.format("Project not found by id: %s", projectId)));

        assertThrows(EntityNotFoundException.class, () -> projectService.getById(projectId),
                String.format("Project not found by id: %s", projectId));
    }

    @Test
    void testGetByIdSuccessfully() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        projectService.getById(projectId);

        assertEquals(projectDto.getName(), projectService.getById(1L).getName());
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
}
