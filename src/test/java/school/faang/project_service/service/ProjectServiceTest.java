package school.faang.project_service.service;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.impl.ProjectServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static faang.school.projectservice.constant.ProjectErrorMessages.PROJECT_WITH_ID_NOT_FOUND;
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
    void getProject_WhenProjectExists_ReturnsProjectDto() {
        long projectId = 1L;
        Project project = createTestProject();
        ProjectDto expectedProjectDto = projectMapper.toDto(project);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        ProjectDto result = projectService.getProject(projectId);

        assertNotNull(result);
        assertEquals(expectedProjectDto, result);

        verify(projectRepository).findById(projectId);
    }

    @Test
    void getProject_WhenProjectDoesNotExist_ThrowsFeignExceptionNotFound() {
        long projectId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        FeignException.NotFound exception = assertThrows(
                FeignException.NotFound.class,
                () -> projectService.getProject(projectId)
        );

        assertEquals(String.format(PROJECT_WITH_ID_NOT_FOUND, projectId), exception.getMessage());
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