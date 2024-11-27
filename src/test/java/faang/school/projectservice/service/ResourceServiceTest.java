package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.ResourceValidator;
import faang.school.projectservice.validator.TeamMemberValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock
    private ResourceRepository resourceRepository;

    @Spy
    private ResourceMapperImpl resourceMapper;

    @Mock
    private ResourceValidator resourceValidator;

    @Mock
    private TeamMemberValidator teamMemberValidator;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private StorageService storageService;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ResourceService resourceService;

    private Long userId;
    private Long projectId;
    private Project project;
    private MockMultipartFile file;
    private Resource resource;
    private TeamMember teamMember = createMockTeamMember();


    @BeforeEach
    void setUp() {
        userId = 1L;
        projectId = 1L;
        project = createMockProject();
        file = createMockMultipartFile();
        resource = createMockResource();
        teamMember = createMockTeamMember();
    }

    @Test
    @DisplayName("Upload file with all valid parameters: success")
    void UploadResource_ValidParameters_Success() {

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(teamMemberService.getTeamMemberByUserId(userId)).thenReturn(teamMember);
        when(teamMemberService.getTeamMemberByUserId(userId)).thenReturn(teamMember);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        ResourceResponseDto result = resourceService.uploadResource(projectId, userId, file);

        assertNotNull(result);
        assertEquals(resource.getId(), result.getId());
        assertEquals(resource.getName(), result.getName());
        assertEquals(resource.getKey(), result.getKey());
        assertEquals(resource.getSize(), result.getSize());
        assertEquals(resource.getType(), result.getType());
        assertEquals(resource.getCreatedAt(), result.getCreatedAt());
        assertEquals(resource.getCreatedBy().getUserId(), result.getCreatedById());
        assertEquals(resource.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(resource.getUpdatedBy().getUserId(), result.getUpdatedById());

        verify(projectValidator, times(1)).validateProjectExistsById(projectId);
        verify(teamMemberValidator, times(1)).validateTeamMemberExistsById(userId);
        verify(resourceValidator, times(1)).validateResourceNotEmpty(file);
        verify(projectValidator, times(1)).validateUserInProjectTeam(userId, project);
        verify(resourceValidator, times(1)).validateEnoughSpaceInStorage(project, file);
    }

    @Test
    @DisplayName("Upload file with invalid project id: fail")
    void UploadResource_InvalidProjectId_Fail() {

        doThrow(new EntityNotFoundException(String.format("Project with id %d doesn't exist", projectId)))
                .when(projectValidator).validateProjectExistsById(projectId);

        RuntimeException ex = assertThrows(EntityNotFoundException.class, () -> resourceService.uploadResource(projectId, userId, file));
        assertEquals(String.format("Project with id %d doesn't exist", projectId), ex.getMessage());

        verify(teamMemberValidator, never()).validateTeamMemberExistsById(userId);
        verify(resourceValidator, never()).validateResourceNotEmpty(file);
        verify(projectValidator, never()).validateUserInProjectTeam(userId, project);
        verify(resourceValidator, never()).validateEnoughSpaceInStorage(project, file);
    }

    @Test
    @DisplayName("Upload file with invalid user id: fail")
    void UploadResource_InvalidUserId_Fail() {

        doThrow(new EntityNotFoundException(String.format("Team member not found, id: %d", userId)))
                .when(teamMemberValidator).validateTeamMemberExistsById(userId);

        RuntimeException ex = assertThrows(EntityNotFoundException.class, () -> resourceService.uploadResource(projectId, userId, file));
        assertEquals(String.format("Team member not found, id: %d", userId), ex.getMessage());

        verify(projectValidator, times(1)).validateProjectExistsById(projectId);
        verify(resourceValidator, never()).validateResourceNotEmpty(file);
        verify(projectValidator, never()).validateUserInProjectTeam(userId, project);
        verify(resourceValidator, never()).validateEnoughSpaceInStorage(project, file);
    }

    private Project createMockProject() {
        return Project.builder()
                .id(1L)
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(null)
                .build();
    }

    private MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "content".getBytes()
        );
    }

    private Resource createMockResource() {
        return Resource.builder()
                .id(1L)
                .name("Test resource")
                .key("test-key")
                .size(BigInteger.valueOf(1024))
                .type(ResourceType.TEXT)
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .createdBy(teamMember)
                .updatedAt(LocalDateTime.now())
                .updatedBy(teamMember)
                .build();
    }

    private TeamMember createMockTeamMember() {
        return TeamMember.builder()
                .id(1L)
                .userId(1L)
                .roles(List.of(TeamRole.OWNER))
                .build();
    }
}