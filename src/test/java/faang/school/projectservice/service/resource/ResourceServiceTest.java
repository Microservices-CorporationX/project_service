package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.PermissionDeniedException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.resource.ResourceDtoMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.AwsS3Service;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @InjectMocks
    private ResourceService resourceService;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private TeamMemberJpaRepository teamMemberRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private AwsS3Service awsS3Service;
    @Mock
    private ResourceDtoMapperImpl resourceDtoMapper;

    @Test
    void uploadFile_shouldUploadResource_whenAllDateIsValid() {
        // given
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Project project = createProject();
        Resource resource = createResource();
        ResourceDto expectedResourceDto = createExpectedResourceDto();

        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(teamMemberRepository.isUserInAnyTeamOfProject(anyLong(), anyLong())).thenReturn(true);
        when(awsS3Service.uploadResource(anyString(), any())).thenReturn(resource);
        when(resourceRepository.save(any())).thenReturn(resource);
        when(resourceDtoMapper.toDto(any())).thenReturn(expectedResourceDto);

        // when
        ResourceDto actualResourceDto = resourceService.uploadResource(project.getId(), 1L, file);

        // then
        verify(projectRepository, times(1)).getProjectById(project.getId());
        verify(teamMemberRepository, times(1)).isUserInAnyTeamOfProject(project.getId(), 1L);
        verify(awsS3Service, times(1)).uploadResource(anyString(), any());
        verify(resourceRepository, times(1)).save(resource);
        verify(resourceDtoMapper, times(1)).toDto(resource);

        assertNotNull(actualResourceDto);
        assertEquals(project.getStorageSize(), actualResourceDto.size());
        assertEquals(expectedResourceDto.id(), actualResourceDto.id());
        assertEquals(expectedResourceDto.name(), actualResourceDto.name());
    }

    @Test
    void uploadFile_shouldThrowPermissionDeniedException_whenUserIsNotProjectMember() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Project project = createProject();

        when(projectRepository.getProjectById(anyLong())).thenReturn(project);

        PermissionDeniedException exception = assertThrows(PermissionDeniedException.class,
                () -> resourceService.uploadResource(project.getId(), 1L, file));

        verify(projectRepository, times(1)).getProjectById(project.getId());

        assertEquals(exception.getClass(), PermissionDeniedException.class);
    }

    @Test
    void updateResource_shouldUploadResource_whenAllDataIsValid() {
        // given
        MultipartFile file = mock(MultipartFile.class);
        TeamMember member = Mockito.mock(TeamMember.class);
        Project project = createProject();
        Resource resource = createResource();
        resource.setProject(project);
        ResourceDto expectedResourceDto = createExpectedResourceDto();

        Resource updatedResource = createResource();
        updatedResource.setName("New name");
        updatedResource.setSize(BigInteger.valueOf(50L));

        when(member.getId()).thenReturn(1L);
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(teamMemberRepository.findByUserId(anyLong())).thenReturn(List.of(member));
        when(awsS3Service.updateResource(any(), any())).thenReturn(updatedResource);
        when(resourceRepository.save(any())).thenReturn(updatedResource);
        when(resourceDtoMapper.toDto(any())).thenReturn(expectedResourceDto);

        // when
        ResourceDto actualResourceDto = resourceService.updateResource(1L, resource.getId(), file);

        // then
        verify(resourceRepository, times(1)).save(any());
        verify(projectRepository, times(1)).getProjectById(anyLong());
        verify(teamMemberRepository, times(1)).findByUserId(anyLong());
        verify(resourceDtoMapper, times(1)).toDto(any());

        assertNotNull(actualResourceDto);
        assertNotEquals(resource.getName(), actualResourceDto.name());
        assertNotEquals(resource.getSize(), actualResourceDto.size());
    }

    @Test
    void updateResource_shouldThrowEntityNotFoundException_whenResourceDoesNotExist() {
        MultipartFile file = Mockito.mock(MultipartFile.class);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> resourceService.updateResource(1L, null, file));

        assertEquals(exception.getClass(), EntityNotFoundException.class);
    }

    @Test
    void updateResource_shouldThrowEntityNotFoundException_whenProjectDoesNotExist() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Resource resource = createResource();
        Project project = createProject();
        resource.setProject(project);

        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
        when(projectRepository.getProjectById(anyLong())).thenThrow(new EntityNotFoundException("Project not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> resourceService.updateResource(1L, resource.getId(), file));

        assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void testDownloadResource_Success() {
        Resource resource = createResource();

        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
        InputStream mockInputStream = mock(InputStream.class);
        when(awsS3Service.downloadResource(resource.getKey())).thenReturn(mockInputStream);

        InputStream result = resourceService.downloadResource(resource.getId());

        verify(resourceRepository).findById(anyLong());
        verify(awsS3Service).downloadResource(resource.getKey());

        assertNotNull(result);
    }

    @Test
    void downloadResource_shouldThrowEntityNotFoundException_whenResourceDoesNotExist() {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> resourceService.downloadResource(1L));

        assertEquals(EntityNotFoundException.class, exception.getClass());
    }

    @Test
    void deleteResource_shouldDeleteResource() {
        TeamMember member = Mockito.mock(TeamMember.class);
        Project project = createProject();
        Resource resource = createResource();
        resource.setProject(project);

        // Подготовка
        when(member.getId()).thenReturn(1L);
        when(resourceRepository.findById(any())).thenReturn(Optional.of(resource));
        when(projectRepository.getProjectById(any())).thenReturn(project);
        when(teamMemberRepository.findByUserId(anyLong())).thenReturn(List.of(member));

        resourceService.deleteResource(resource.getId(), 1L);

        verify(resourceRepository).findById(anyLong());
        verify(projectRepository).getProjectById(anyLong());
        assertEquals(ResourceStatus.DELETED, resource.getStatus());
        assertNull(resource.getKey());
        assertNull(resource.getSize());
    }

    private ResourceDto createExpectedResourceDto(){
        return ResourceDto.builder()
                .id(1L)
                .name("File")
                .name("File")
                .size(BigInteger.TEN)
                .build();
    }

    private Resource createResource(){
        return Resource.builder()
                .id(1L)
                .createdBy(1L)
                .key("test")
                .size(BigInteger.TEN)
                .name("File")
                .build();
    }

    private Project createProject(){
        return Project.builder()
                .id(1L)
                .name("TestProject")
                .ownerId(1L)
                .storageSize(BigInteger.valueOf(10))
                .maxStorageSize(BigInteger.valueOf(2048))
                .build();
    }
}