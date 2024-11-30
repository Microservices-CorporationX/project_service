package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.team.TeamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @InjectMocks
    private ResourceService resourceService;
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
    private TeamServiceImpl teamService;
    @Captor
    private ArgumentCaptor<MultipartFile> fileCaptor;
    @Captor
    private ArgumentCaptor<Project> projectCaptor;
    @Captor
    private ArgumentCaptor<Resource> resourceCaptor;

    private Project project = new Project();

    private MultipartFile multipartFile;
    private TeamMember teamMember;
    private Resource resource;


    @BeforeEach
    public void setUp() {
        // Создание MockMultipartFile
        multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());

        teamMember = new TeamMember();
        teamMember.setId(2L);
        teamMember.setId(2L);
        teamMember.setRoles(new ArrayList<>());

        resource = new Resource();
        resource.setProject(project);
        resource.setSize(BigInteger.valueOf(2));
        resource.setCreatedBy(teamMember);
        resource.setKey("key");

        project.setMaxStorageSize(BigInteger.valueOf(2000000));
        project.setId(2L);

    }

    @Test
    void testMemoryLimit() {
        project.setStorageSize(BigInteger.valueOf(1999999));
        Mockito.when(projectService.getProjectEntityById(2L)).thenReturn(project);

        assertThrows(IllegalArgumentException.class,
                () -> resourceService.addResource(2L, multipartFile));
    }

    @Test
    void testCorrectWorkAddResource() {
        project.setStorageSize(BigInteger.valueOf(1));
        Mockito.when(projectService.getProjectEntityById(2L)).thenReturn(project);
        Mockito.when(userContext.getUserId()).thenReturn(4L);
        Mockito.when(teamService.findMemberByUserIdAndProjectId(4L, 2L)).thenReturn(Optional.of(teamMember));
        Mockito.when(s3Service.addResource(any(MultipartFile.class), ArgumentMatchers.anyString())).thenReturn(resource);

        ResourceDto resourceDto = resourceService.addResource(2L, multipartFile);
        Mockito.verify(s3Service).addResource(fileCaptor.capture(), ArgumentMatchers.anyString());
        Mockito.verify(resourceRepository).save(resourceCaptor.capture());
        Mockito.verify(projectService).saveProject(project);

        Resource result = resourceCaptor.getValue();

        assertNotNull(resourceDto.getProjectId());
        assertEquals(fileCaptor.getValue(), multipartFile);
        assertNotNull(result.getCreatedBy());
        assertNotNull(result.getUpdatedBy());
        assertNotNull(result.getProject());

    }

    @Test
    void testMemoryLimitForUpdateResource() {
        project.setStorageSize(BigInteger.valueOf(1999999));

        Mockito.when(resourceRepository.findById(3L)).thenReturn(Optional.of(resource));

        assertThrows(IllegalArgumentException.class,
                () -> resourceService.updateResource(3L, multipartFile));
    }

    @Test
    void testCorrectWordUpdateResource() {
        project.setStorageSize(BigInteger.valueOf(1));

        Mockito.when(resourceRepository.findById(3L)).thenReturn(Optional.of(resource));
        Mockito.when(userContext.getUserId()).thenReturn(4L);
        Mockito.when(teamService.findMemberByUserIdAndProjectId(4L, 2L)).thenReturn(Optional.of(teamMember));

        ResourceDto result = resourceService.updateResource(3, multipartFile);
        Mockito.verify(resourceRepository).save(resourceCaptor.capture());
        Mockito.verify(projectService).saveProject(projectCaptor.capture());
        Mockito.verify(s3Service).updateResource(fileCaptor.capture(), ArgumentMatchers.anyString());

        Resource updatedResource = resourceCaptor.getValue();
        Project updatedProject = projectCaptor.getValue();

        assertEquals(updatedResource.getUpdatedBy(), teamMember);
        assertNotNull(updatedResource.getUpdatedAt());
        assertEquals(updatedResource.getSize(), resource.getSize());
        assertEquals(updatedProject.getStorageSize(), project.getStorageSize());
        assertEquals(result.getSize(), resource.getSize());
    }

    @Test
    void testInvalidAccess() {
        teamMember.setUserId(3L);
        Mockito.when(resourceRepository.findById(3L)).thenReturn(Optional.of(resource));
        Mockito.when(userContext.getUserId()).thenReturn(4L);
        Mockito.when(teamService.findMemberByUserIdAndProjectId(4L, 2L)).thenReturn(Optional.of(teamMember));

        assertThrows(IllegalArgumentException.class,
                () -> resourceService.removeResource(3L));
    }

    @Test
    void testCorrectRemoveResource() {
        teamMember.setUserId(4L);
        project.setStorageSize(BigInteger.valueOf(15));
        Mockito.when(resourceRepository.findById(3L)).thenReturn(Optional.of(resource));
        Mockito.when(userContext.getUserId()).thenReturn(4L);
        Mockito.when(teamService.findMemberByUserIdAndProjectId(4L, 2L)).thenReturn(Optional.of(teamMember));

        ResourceDto result = resourceService.removeResource(3L);

        Mockito.verify(resourceRepository).save(resourceCaptor.capture());
        Mockito.verify(projectService).saveProject(projectCaptor.capture());

        Resource updatedResource = resourceCaptor.getValue();
        Project updatedProject = projectCaptor.getValue();

        assertEquals(updatedResource.getStatus(), ResourceStatus.DELETED);
        assertNotNull(updatedResource.getUpdatedAt());
        assertEquals(updatedResource.getUpdatedBy(), teamMember);
        assertNull(updatedResource.getKey());
        assertEquals(updatedResource.getSize(), BigInteger.valueOf(0));
        assertEquals(updatedProject.getStorageSize(), BigInteger.valueOf(13));
    }
    @Test
    void testGeneratePresignedUrl(){
        Mockito.when(resourceRepository.findById(3L)).thenReturn(Optional.of(resource));
        Mockito.when(s3Service.generatePresignedUrl(resource.getKey())).thenReturn("Url");

        String result = resourceService.generatePresignedUrl(3);
        assertEquals(result,"Url");
    }
}