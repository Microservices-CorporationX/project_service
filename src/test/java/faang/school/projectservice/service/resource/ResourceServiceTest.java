package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.team.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    private static final String RESOURCE_FOLDER_NAME = "project-%d-files";

    @Mock
    private ProjectService projectService;
    @Mock
    private S3Service s3Service;
    @Mock
    private UserContext userContext;
    @Mock
    private ResourceRepository resourceRepository;
    @Spy
    private ResourceMapperImpl resourceMapper;
    @Mock
    private TeamMemberService teamMemberService;
    @InjectMocks
    private ResourceService resourceService;
    @Captor
    private ArgumentCaptor<Resource> resourceArgumentCaptor;

    @Test
    void testAddResourceAdded() {
        long userId = 1L;
        long projectId = 1L;
        long teamMemberId = 1L;
        List<TeamRole> roles = List.of(TeamRole.MANAGER, TeamRole.ANALYST);
        TeamMember teamMember = TeamMember.builder().userId(userId).id(teamMemberId).roles(roles).build();
        Project project = Project.builder().id(projectId).build();
        String fileContent = "content";
        String fileName = "file name";
        String originalFileName = "original";
        String contentType = "content type";
        Resource resource = new Resource();
        MultipartFile file = new MockMultipartFile(fileName, originalFileName,
                contentType, fileContent.getBytes(StandardCharsets.UTF_8));
        String folder = String.format(RESOURCE_FOLDER_NAME, projectId);

        when(userContext.getUserId()).thenReturn(userId);
        when(teamMemberService.validateUserIsProjectMember(userId, projectId)).thenReturn(teamMember);
        when(projectService.changeStorageSize(projectId, file.getSize())).thenReturn(project);
        when(s3Service.uploadFile(file, folder)).thenReturn(resource);
        when(resourceRepository.save(resource)).thenReturn(resource);

        ResourceDto result = resourceService.addResource(projectId, file);

        verify(teamMemberService).validateUserIsProjectMember(userId, projectId);
        verify(projectService).changeStorageSize(projectId, file.getSize());
        verify(s3Service).uploadFile(file, folder);
        verify(resourceRepository).save(resourceArgumentCaptor.capture());
        Resource capturedResource = resourceArgumentCaptor.getValue();
        assertEquals(project, capturedResource.getProject());
        assertEquals(teamMember, capturedResource.getCreatedBy());
        assertEquals(teamMember, capturedResource.getUpdatedBy());
        assertArrayEquals(teamMember.getRoles().toArray(new TeamRole[0]),
                capturedResource.getAllowedRoles().toArray(new TeamRole[0]));
        assertEquals(resourceMapper.toDto(capturedResource), result);
    }

    @Test
    void testDownloadResourceDownloaded() throws IOException {
        String key = "key";
        long userId = 1L;
        Project project = Project.builder().id(1L).build();
        Resource resource = Resource.builder().id(1L).key(key).project(project).build();
        when(resourceRepository.getReferenceById(resource.getId())).thenReturn(resource);
        when(userContext.getUserId()).thenReturn(userId);
        InputStream inputStream = IOUtils.toInputStream("some data", "utf-8");
        when(s3Service.downloadFile(key)).thenReturn(inputStream);
        InputStream result = resourceService.downloadResource(resource.getId());
        verify(teamMemberService).validateUserIsProjectMember(userId, project.getId());
        assertEquals(inputStream, result);
    }

    @Test
    void testDeleteResourceDeleted() {
        Project project = Project.builder().id(1L).build();
        Resource resource = Resource.builder().id(1L).project(project).build();
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.of(resource));
        long userId = 3L;
        when(userContext.getUserId()).thenReturn(userId);
        TeamMember teamMember = TeamMember.builder().roles(List.of(TeamRole.MANAGER, TeamRole.ANALYST)).build();
        when(teamMemberService.validateUserIsProjectMember(userId, resource.getProject().getId()))
                .thenReturn(teamMember);

        resourceService.deleteResource(resource.getId());

        verify(resourceRepository).save(resource);
        assertEquals(ResourceStatus.DELETED, resource.getStatus());
    }

    @Test
    void testDeleteResourceWithNotFound() {
        when(resourceRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> resourceService.deleteResource(1L));
        assertEquals("Resource not found", exception.getMessage());
    }

    @Test
    void testDeleteResourceWithNotAllowed() {
        Project project = Project.builder().id(1L).build();
        Resource resource = Resource.builder().id(1L).createdBy(new TeamMember()).project(project).build();
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.of(resource));
        long userId = 3L;
        when(userContext.getUserId()).thenReturn(userId);
        TeamMember teamMember = TeamMember.builder().roles(List.of(TeamRole.ANALYST)).build();
        when(teamMemberService.validateUserIsProjectMember(userId, resource.getProject().getId()))
                .thenReturn(teamMember);

        AccessDeniedException exception =
                assertThrows(AccessDeniedException.class, () -> resourceService.deleteResource(resource.getId()));
        assertEquals("Need to be manager or file creator to delete file", exception.getMessage());
    }

    @Test
    void testUpdateResourceUpdated() {
        long userId = 1L;
        long projectId = 1L;
        long teamMemberId = 1L;
        List<TeamRole> roles = List.of(TeamRole.MANAGER, TeamRole.ANALYST);
        TeamMember teamMember = TeamMember.builder().userId(userId).id(teamMemberId).roles(roles).build();
        Project project = Project.builder().id(projectId).build();
        String fileContent = "content";
        String fileName = "file name";
        String originalFileName = "original";
        String contentType = "content type";
        Resource resource = Resource.builder().id(1L).size(BigInteger.TWO).project(project).build();
        MultipartFile file = new MockMultipartFile(fileName, originalFileName,
                contentType, fileContent.getBytes(StandardCharsets.UTF_8));
        String folder = String.format(RESOURCE_FOLDER_NAME, projectId);

        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.of(resource));
        when(userContext.getUserId()).thenReturn(userId);
        when(teamMemberService.validateUserIsProjectMember(userId, projectId)).thenReturn(teamMember);
        long changeInSize = BigInteger.valueOf(file.getSize()).subtract(resource.getSize()).longValue();
        when(projectService.changeStorageSize(projectId, changeInSize)).thenReturn(project);
        String newKey = "new key";
        Resource resourceFromS3 = Resource.builder().key(newKey).build();
        when(s3Service.uploadFile(file, folder)).thenReturn(resourceFromS3);

        resourceService.updateResource(resource.getId(), file);
        verify(resourceRepository).save(resource);
        assertEquals(newKey, resource.getKey());
    }
}