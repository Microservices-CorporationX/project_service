package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.CreateProjectRequest;
import faang.school.projectservice.dto.project.DeleteProjectRequest;
import faang.school.projectservice.dto.project.FilterProjectRequest;
import faang.school.projectservice.dto.project.ProjectResponse;
import faang.school.projectservice.dto.project.UpdateProjectRequest;
import faang.school.projectservice.exception.ProjectAlreadyExistsException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.project.ProjectFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_Success() {
        long ownerId = 1;
        long projectId = 100;
        String projectName = "testName";
        CreateProjectRequest createProjectRequest = CreateProjectRequest.builder()
                .ownerId(ownerId)
                .name(projectName)
                .build();
        Project project = Project.builder()
                .id(projectId)
                .ownerId(ownerId)
                .name(projectName)
                .build();

        when(userServiceClient.getUser(ownerId)).thenReturn(UserDto.builder().id(ownerId).build());
        when(projectRepository.existsByOwnerIdAndName(ownerId, projectName)).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse response = projectService.createProject(createProjectRequest);

        assertNotNull(response);
        assertEquals(projectId, response.id());
        assertEquals(projectName, response.name());
        verify(projectRepository).save(any(Project.class));
        verify(userServiceClient).getUser(ownerId);
    }

    @Test
    void createProject_ProjectAlreadyExistsException() {
        long ownerId = 1;
        String projectName = "duplicateName";
        CreateProjectRequest createProjectRequest = CreateProjectRequest.builder()
                .ownerId(ownerId)
                .name(projectName)
                .build();

        when(userServiceClient.getUser(ownerId)).thenReturn(UserDto.builder().id(ownerId).build());
        when(projectRepository.existsByOwnerIdAndName(ownerId, projectName)).thenReturn(true);

        assertThrows(ProjectAlreadyExistsException.class, () -> projectService.createProject(createProjectRequest));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void createProject_UserNotFoundException() {
        long ownerId = 1;
        CreateProjectRequest createProjectRequest = CreateProjectRequest.builder()
                .ownerId(ownerId)
                .name("testName")
                .build();

        when(userServiceClient.getUser(ownerId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> projectService.createProject(createProjectRequest));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void updateProject_Success() {
        long projectId = 10;
        long ownerId = 1;
        UpdateProjectRequest updateProjectRequest = UpdateProjectRequest.builder()
                .id(projectId)
                .ownerId(ownerId)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Project existingProject = Project.builder()
                .id(projectId)
                .ownerId(ownerId)
                .name("OldName")
                .description("OldDescription")
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        when(userServiceClient.getUser(ownerId)).thenReturn(UserDto.builder().id(ownerId).build());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse response = projectService.updateProject(updateProjectRequest);

        assertNotNull(response);
        assertEquals(projectId, response.id());
        assertEquals("UpdatedName", response.name());
        assertEquals("UpdatedDescription", response.description());
        verify(projectRepository).findById(projectId);
        verify(projectRepository).save(existingProject);
    }

    @Test
    void updateProject_ProjectNotFoundException() {
        long projectId = 10;
        UpdateProjectRequest updateProjectRequest = UpdateProjectRequest.builder()
                .id(projectId)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projectService.updateProject(updateProjectRequest));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void filterProjects_Success() {
        long userId = 1;
        FilterProjectRequest filterRequest = FilterProjectRequest.builder().build();

        Project publicProject = Project.builder()
                .id(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .name("Public Project")
                .build();

        Project privateProject = Project.builder()
                .id(2L)
                .visibility(ProjectVisibility.PRIVATE)
                .ownerId(userId)
                .name("Private Project")
                .build();

        when(userServiceClient.getUser(userId)).thenReturn(UserDto.builder().id(userId).build());
        when(projectRepository.findAll()).thenReturn(List.of(publicProject, privateProject));

        ProjectFilter filter = mock(ProjectFilter.class);
        when(filter.filter(any(), eq(filterRequest))).thenAnswer(invocation -> invocation.getArgument(0));

        List<ProjectFilter> mockFilters = List.of(filter);
        ReflectionTestUtils.setField(projectService, "projectFilters", mockFilters);
        List<ProjectResponse> response = projectService.filterProjects(userId, filterRequest);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(projectRepository).findAll();
    }

    @Test
    void filterProjects_UserCannotSeePrivateProject() {
        long userId = 1;
        long ownerId = 2;
        FilterProjectRequest filterRequest = FilterProjectRequest.builder().build();

        Project publicProject = Project.builder()
                .id(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .name("Public Project")
                .build();

        Project privateProject = Project.builder()
                .id(2L)
                .visibility(ProjectVisibility.PRIVATE)
                .ownerId(ownerId)
                .name("Private Project")
                .build();

        when(userServiceClient.getUser(userId)).thenReturn(UserDto.builder().id(userId).build());
        when(projectRepository.findAll()).thenReturn(List.of(publicProject, privateProject));

        ProjectFilter mockFilter = mock(ProjectFilter.class);
        when(mockFilter.filter(any(), eq(filterRequest)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        List<ProjectFilter> mockFilters = List.of(mockFilter);
        ReflectionTestUtils.setField(projectService, "projectFilters", mockFilters);

        List<ProjectResponse> response = projectService.filterProjects(userId, filterRequest);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertTrue(response.stream().anyMatch(project -> project.name().equals("Public Project")));
        assertFalse(response.stream().anyMatch(project -> project.name().equals("Private Project")));

        verify(userServiceClient).getUser(userId);
        verify(projectRepository).findAll();
        verify(mockFilter).filter(any(), eq(filterRequest));
    }

    @Test
    void deleteProject_Success() {
        long ownerId = 1;
        long projectId = 100;
        DeleteProjectRequest deleteProjectRequest = DeleteProjectRequest.builder()
                .ownerId(ownerId)
                .id(projectId)
                .build();

        Project project = Project.builder()
                .id(projectId)
                .ownerId(ownerId)
                .build();

        when(userServiceClient.getUser(ownerId)).thenReturn(UserDto.builder().id(ownerId).build());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertDoesNotThrow(() -> projectService.deleteProject(deleteProjectRequest));

        verify(projectRepository).delete(project);
        verify(userServiceClient).getUser(ownerId);
    }

    @Test
    void deleteProject_UserNotOwnerException() {
        long ownerId = 1;
        long projectId = 100;
        long anotherUserId = 2;
        DeleteProjectRequest deleteProjectRequest = DeleteProjectRequest.builder()
                .ownerId(anotherUserId)
                .id(projectId)
                .build();

        Project project = Project.builder()
                .id(projectId)
                .ownerId(ownerId)
                .build();

        when(userServiceClient.getUser(anotherUserId)).thenReturn(UserDto.builder().id(anotherUserId).build());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                projectService.deleteProject(deleteProjectRequest));

        assertEquals("Пользователь не владелец проекта", exception.getMessage());
        verify(projectRepository, never()).delete(any(Project.class));
    }
}