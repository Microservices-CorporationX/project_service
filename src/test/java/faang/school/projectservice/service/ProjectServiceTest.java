package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

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
}
