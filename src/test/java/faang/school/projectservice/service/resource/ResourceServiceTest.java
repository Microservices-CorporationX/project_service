package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.dto.resource.ResourceDtoStored;
import faang.school.projectservice.exception.FileWriteReadS3Exception;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validation.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    @Mock
    private MultipartFile file;

    @Mock
    private UserValidator userValidator;

    @Mock
    private S3Service s3Service;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ResourceMapper resourceMapper;

    @InjectMocks
    private ResourceService resourceService;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<String> resourceKeyCaptor;

    @Captor
    private ArgumentCaptor<Resource> resourceCaptorFirst;

    @Captor
    private ArgumentCaptor<Resource> resourceCaptorSecond;

    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    private String bucketName;
    private String folder;
    private String projectName = "project_test";
    private String fileName = "test_file";
    private String fileType = "pdf";

    private final long fileSize = 1000L;

    private final long userId = 1L;
    private final long projectId = 2L;
    private final long teamMemberId = 3L;
    private final long resourceId = 4L;


    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(resourceService, "bucketName", "resourceBucket");
        ReflectionTestUtils.setField(resourceService, "folder", "resourceFolder");

        bucketName = "resourceBucket";
        folder = "resourceFolder";
    }

    @Test
    void downloadResourceSuccessTest() {
        String key = "resourceKey";
        Resource resource = getResource();
        resource.setKey(key);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(resourceMapper.toResourceDtoStored(resourceCaptorSecond.capture())).thenReturn(ResourceDtoStored.builder().build());
        when(s3Service.fromS3File(eq(bucketName), keyCaptor.capture()))
                .thenReturn(new ByteArrayInputStream(new byte[0]));

        ResourceDtoStored actualResult = resourceService.downloadResource(resourceId);

        assertNotNull(actualResult);
        verify(resourceRepository, times(1)).findById(resourceId);
        verify(resourceMapper, times(1)).toResourceDtoStored(any());
        verify(s3Service, times(1)).fromS3File(any(), any());
        assertThat(keyCaptor.getValue()).isEqualTo(key);
        assertThat(resourceCaptorSecond.getValue().getId()).isEqualTo(resource.getId());
    }

    @Test
    void downloadResourceNotFoundFailTest() {

        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> resourceService.downloadResource(resourceId));

        assertEquals(String.format(ResourceService.RESOURCE_NOT_FOUND_BY_ID, resourceId), entityNotFoundException.getMessage());
    }

    @Test
    void downloadResourceFailTest() {
        String key = "resourceKey";
        Resource resource = getResource();
        resource.setKey(key);
        String message = String.format(ResourceService.RESOURCE_NOT_FOUND_BY_ID, resourceId);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(s3Service.fromS3File(eq(bucketName), keyCaptor.capture()))
                .thenThrow(new EntityNotFoundException(message));

        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> resourceService.downloadResource(resourceId));

        assertEquals(message, entityNotFoundException.getMessage());
    }

    @Test
    void addResourceSuccessTest() throws IOException {
        TeamMember teamMember = getTeamMember();
        Project project = getProject();

        doNothing().when(userValidator).validateUserId(anyLong());
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(resourceMapper.toDto(resourceCaptorSecond.capture())).thenReturn(ResourceDto.builder().build());
        when(file.getContentType()).thenReturn(fileType);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getSize()).thenReturn(fileSize);

        doNothing().when(s3Service).toS3File(
                eq(file),
                eq(bucketName),
                keyCaptor.capture()
        );

        when(projectRepository.save(project)).thenReturn(project);
        when(resourceRepository.save(resourceCaptorFirst.capture())).thenReturn(any());

        ResourceDto actualResult = resourceService.addResource(projectId, userId, file);

        assertNotNull(actualResult);
        verify(userValidator, times(1)).validateUserId(anyLong());
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
        verify(projectRepository, times(1)).getProjectById(projectId);
        verify(resourceMapper, times(1)).toDto(any());
        verify(resourceRepository, times(1)).save(any());
        verify(projectRepository, times(1)).save(any());
        assertThat(keyCaptor.getValue()).contains("resourceFolder_project_test_2");
        assertThat(keyCaptor.getValue()).contains("test_file");
        assertThat(resourceCaptorFirst.getValue().getType()).isEqualTo(ResourceType.PDF);
        assertThat(resourceCaptorFirst.getValue().getName()).isEqualTo(fileName);
        assertThat(resourceCaptorFirst.getValue().getSize()).isEqualTo(fileSize);
        assertThat(resourceCaptorFirst.getValue().getStatus()).isEqualTo(ResourceStatus.ACTIVE);
        assertThat(resourceCaptorFirst.getValue().getProject().getId()).isEqualTo(projectId);
    }

    @Test
    void addResourceFailTest() throws IOException {
        TeamMember teamMember = getTeamMember();
        Project project = getProject();
        String message = "Error add!";

        doNothing().when(userValidator).validateUserId(anyLong());
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        doThrow(new FileWriteReadS3Exception(message)).when(s3Service).toS3File(
                any(),
                any(),
                any()
        );

        FileWriteReadS3Exception exception =
                assertThrows(FileWriteReadS3Exception.class, () -> resourceService.addResource(projectId, userId, file));

        assertEquals(message, exception.getMessage());
        verify(userValidator, times(1)).validateUserId(anyLong());
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
        verify(projectRepository, times(1)).getProjectById(projectId);
        verify(resourceMapper, never()).toDto(any());
        verify(resourceRepository, never()).save(any());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void addResourceTeamMemberNotFoundFailTest() {
        doNothing().when(userValidator).validateUserId(anyLong());
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId))
                .thenThrow(new EntityNotFoundException(String.format(ResourceService.TEAM_MEMBER_NOT_FOUND, userId, projectId)));

        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> resourceService.addResource(projectId, userId, file)
                );

        assertEquals(String.format(ResourceService.TEAM_MEMBER_NOT_FOUND, userId, projectId), entityNotFoundException.getMessage());
        verify(userValidator, times(1)).validateUserId(anyLong());
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
    }

    @Test
    void addResourceStorageSizeExceededFailTest() {
        TeamMember teamMember = getTeamMember();
        Project project = getProject();
        project.setMaxStorageSize(BigInteger.valueOf(200000L));

        doNothing().when(userValidator).validateUserId(anyLong());
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(file.getSize()).thenReturn(250000L);

        ValidationException validationException =
                assertThrows(ValidationException.class, () -> resourceService.addResource(projectId, userId, file)
                );

        assertEquals(ResourceService.EXCEEDING_MAXIMUM_STORAGE_SIZE, validationException.getMessage());
        verify(userValidator, times(1)).validateUserId(anyLong());
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
    }

    @Test
    void updateResourceSuccessTest() throws IOException {
        TeamMember teamMember = getTeamMember();
        teamMember.getRoles().add(TeamRole.MANAGER);
        Project project = getProject();
        project.setStorageSize(BigInteger.valueOf(2500L));
        Resource resource = getResource();
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setSize(BigInteger.valueOf(1200L));
        resource.setProject(project);

        doNothing().when(userValidator).validateUserId(anyLong());
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(resourceMapper.toDto(resourceCaptorSecond.capture())).thenReturn(ResourceDto.builder().build());
        when(file.getContentType()).thenReturn(fileType);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getSize()).thenReturn(fileSize);

        doNothing().when(s3Service).deleteFile(eq(bucketName), resourceKeyCaptor.capture());

        doNothing().when(s3Service).toS3File(
                eq(file),
                eq(bucketName),
                keyCaptor.capture()
        );

        when(projectRepository.save(projectCaptor.capture())).thenReturn(project);
        when(resourceRepository.save(resourceCaptorFirst.capture())).thenReturn(any());

        ResourceDto actualResult = resourceService.updateResource(resourceId, userId, file);

        assertNotNull(actualResult);
        verify(userValidator, times(1)).validateUserId(anyLong());
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
        verify(resourceMapper, times(1)).toDto(any());
        verify(resourceRepository, times(1)).save(any());
        verify(projectRepository, times(1)).save(any());
        assertThat(keyCaptor.getValue()).contains("resourceFolder_project_test_2");
        assertThat(keyCaptor.getValue()).contains("test_file");
        assertThat(resourceCaptorFirst.getValue().getType()).isEqualTo(ResourceType.PDF);
        assertThat(resourceCaptorFirst.getValue().getName()).isEqualTo(fileName);
        assertThat(resourceCaptorFirst.getValue().getSize()).isEqualTo(fileSize);
        assertThat(resourceCaptorFirst.getValue().getStatus()).isEqualTo(ResourceStatus.ACTIVE);
        assertThat(projectCaptor.getValue().getStorageSize().longValue()).isEqualTo(2300L);
        assertThat(resourceCaptorFirst.getValue().getProject().getId()).isEqualTo(projectId);
    }

    @Test
    void updateResourceDeleteResourceFailTest() {
        TeamMember teamMember = getTeamMember();
        teamMember.getRoles().add(TeamRole.MANAGER);
        Project project = getProject();
        project.setStorageSize(BigInteger.valueOf(2500L));
        Resource resource = getResource();
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setSize(BigInteger.valueOf(1200L));
        resource.setProject(project);

        String message = "Error add!";

        doNothing().when(userValidator).validateUserId(anyLong());
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(file.getSize()).thenReturn(fileSize);

        doThrow(new RuntimeException(message)).when(s3Service).deleteFile(
                any(),
                any()
        );

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> resourceService.updateResource(resourceId, userId, file));

        assertEquals(message, exception.getMessage());
    }

    @Test
    void updateResourceFailTest() throws IOException {
        TeamMember teamMember = getTeamMember();
        teamMember.getRoles().add(TeamRole.MANAGER);
        Project project = getProject();
        project.setStorageSize(BigInteger.valueOf(2500L));
        Resource resource = getResource();
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setSize(BigInteger.valueOf(1200L));
        resource.setProject(project);
        String message = "Error update!";

        doNothing().when(userValidator).validateUserId(anyLong());
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getSize()).thenReturn(fileSize);

        doNothing().when(s3Service).deleteFile(eq(bucketName), resourceKeyCaptor.capture());

        doThrow(new FileWriteReadS3Exception(message)).when(s3Service).toS3File(
                any(),
                any(),
                any()
        );

        FileWriteReadS3Exception exception =
                assertThrows(FileWriteReadS3Exception.class, () -> resourceService.updateResource(resourceId, userId, file));

        assertEquals(message, exception.getMessage());
    }

    @Test
    void updateResourceTeamMemberNotFoundFailTest() {
        Project project = getProject();
        Resource resource = getResource();
        resource.setProject(project);
        TeamMember teamMember = getTeamMember();
        teamMember.getRoles().add(TeamRole.TESTER);

        doNothing().when(userValidator).validateUserId(anyLong());
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId))
                .thenThrow(new EntityNotFoundException(String.format(ResourceService.TEAM_MEMBER_NOT_FOUND, userId, projectId)));

        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> resourceService.updateResource(resourceId, userId, file)
                );

        assertEquals(String.format(ResourceService.TEAM_MEMBER_NOT_FOUND, userId, projectId), entityNotFoundException.getMessage());
        verify(userValidator, times(1)).validateUserId(anyLong());
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
    }

    @Test
    void updateResourceResourceNotFoundFailTest() {
        doNothing().when(userValidator).validateUserId(anyLong());
        when(resourceRepository.findById(resourceId))
                .thenThrow(new EntityNotFoundException(String.format(ResourceService.RESOURCE_NOT_FOUND_BY_ID, resourceId)));

        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> resourceService.updateResource(resourceId, userId, file)
                );

        assertEquals(String.format(ResourceService.RESOURCE_NOT_FOUND_BY_ID, resourceId), entityNotFoundException.getMessage());
        verify(userValidator, times(1)).validateUserId(anyLong());

    }

    @Test
    void updateResourcePermissionsFailFailTest() {
        Project project = getProject();
        Resource resource = getResource();
        resource.setProject(project);
        TeamMember teamMember = getTeamMember();
        teamMember.getRoles().add(TeamRole.TESTER);

        doNothing().when(userValidator).validateUserId(anyLong());
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);

        ValidationException validationException =
                assertThrows(ValidationException.class, () -> resourceService.updateResource(resourceId, userId, file)
                );

        assertEquals(ResourceService.CHECK_PERMISSIONS_ERROR, validationException.getMessage());
        verify(userValidator, times(1)).validateUserId(anyLong());
    }

    @Test
    void deleteResourceSuccessTest() {
        TeamMember teamMember = getTeamMember();
        teamMember.getRoles().add(TeamRole.MANAGER);
        Project project = getProject();
        project.setStorageSize(BigInteger.valueOf(2500L));
        Resource resource = getResource();
        resource.setSize(BigInteger.valueOf(1000L));
        resource.setProject(project);

        doNothing().when(userValidator).validateUserId(anyLong());
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);
        when(resourceRepository.getResourceKeyById(resourceId)).thenReturn(Optional.of("resource_key"));
        doNothing().when(s3Service).deleteFile(
                eq(bucketName),
                keyCaptor.capture()
        );

        when(projectRepository.save(projectCaptor.capture())).thenReturn(project);
        when(resourceRepository.save(resourceCaptorFirst.capture())).thenReturn(resource);

        resourceService.deleteResource(resourceId, userId);

        verify(userValidator, times(1)).validateUserId(anyLong());
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
        verify(resourceRepository, times(1)).findById(any());
        verify(resourceRepository, times(1)).save(any());
        verify(projectRepository, times(1)).save(any());
        assertThat(projectCaptor.getValue().getStorageSize().longValue()).isEqualTo(1500L);
        assertThat(resourceCaptorFirst.getValue().getSize().longValue()).isEqualTo(0L);
    }

    @Test
    void deleteResourceResourceNotFoundFailTest() {
        doNothing().when(userValidator).validateUserId(anyLong());
        when(resourceRepository.findById(resourceId))
                .thenThrow(new EntityNotFoundException(String.format(ResourceService.RESOURCE_NOT_FOUND_BY_ID, resourceId)));

        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> resourceService.deleteResource(resourceId, userId)
                );

        assertEquals(String.format(ResourceService.RESOURCE_NOT_FOUND_BY_ID, resourceId), entityNotFoundException.getMessage());
        verify(userValidator, times(1)).validateUserId(anyLong());
    }

    @Test
    void deleteResourcePermissionsFailTest() {
        Project project = getProject();
        Resource resource = getResource();
        resource.setProject(project);
        TeamMember teamMember = getTeamMember();
        teamMember.getRoles().add(TeamRole.TESTER);

        doNothing().when(userValidator).validateUserId(anyLong());
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);

        ValidationException validationException =
                assertThrows(ValidationException.class, () -> resourceService.deleteResource(resourceId, userId)
                );

        assertEquals(ResourceService.CHECK_PERMISSIONS_ERROR, validationException.getMessage());
        verify(userValidator, times(1)).validateUserId(anyLong());
    }

    @Test
    void deleteResourceKeyNotFoundFailTest() {
        TeamMember teamMember = getTeamMember();
        teamMember.getRoles().add(TeamRole.MANAGER);
        Project project = getProject();
        project.setStorageSize(BigInteger.valueOf(2500L));
        Resource resource = getResource();
        resource.setSize(BigInteger.valueOf(1000L));
        resource.setProject(project);

        doNothing().when(userValidator).validateUserId(anyLong());
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);
        when(resourceRepository.getResourceKeyById(resourceId))
                .thenThrow(new EntityNotFoundException(String.format(ResourceService.RESOURCE_KEY_IS_NULL, resourceId)));

        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> resourceService.deleteResource(resourceId, userId)
                );

        assertEquals(String.format(ResourceService.RESOURCE_KEY_IS_NULL, resourceId), entityNotFoundException.getMessage());
        verify(userValidator, times(1)).validateUserId(anyLong());
    }


    private TeamMember getTeamMember() {
        return TeamMember.builder()
                .roles(new ArrayList<>())
                .id(teamMemberId)
                .build();
    }

    private Project getProject() {
        return Project.builder()
                .storageSize(BigInteger.valueOf(0))
                .maxStorageSize(BigInteger.valueOf(ResourceService.TWO_GB_IN_BYTES))
                .name(projectName)
                .id(projectId)
                .build();
    }

    private Resource getResource() {
        return Resource.builder()
                .id(resourceId)
                .allowedRoles(List.of(TeamRole.OWNER))
                .build();
    }
}
