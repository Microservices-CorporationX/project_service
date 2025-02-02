package school.faang.project_service.service;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapperImpl projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void getProject_WhenProjectExists_ReturnsResponseEntityWithProjectDto() {
        long projectId = 1L;
        Project project = createTestProject();
        ProjectDto expectedProjectDto = projectMapper.toDto(project);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ResponseEntity<ProjectDto> response = projectService.getProject(projectId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProjectDto, response.getBody());

        verify(projectRepository).findById(projectId);
    }

    @Test
    void getProject_WhenProjectDoesNotExist_ReturnsNotFoundResponse() {
        long projectId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        ResponseEntity<ProjectDto> response = projectService.getProject(projectId);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(projectRepository).findById(projectId);
        verifyNoInteractions(projectMapper);
    }

    private Project createTestProject() {
        return Project.builder()
                .id(1L)
                .name("Test Project")
                .ownerId(1L)
                .build();
    }

}