package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.RequestDeleteResourceDto;
import faang.school.projectservice.dto.resource.ResponseResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Util;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import faang.school.projectservice.validator.resource.ResourceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @InjectMocks
    private ResourceService resourceService;

    @Mock
    private ProjectService projectService;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private S3Util s3Util;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ResourceValidator resourceValidator;
    @Mock
    private ResourceMapper resourceMapper;
    @Mock
    private MultipartFile multipartFile;

    @Test
    void testUploadResources_Positive() {
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .roles(new ArrayList<>(List.of(TeamRole.MANAGER)))
                .build();
        Project project = Project.builder()
                .id(4L)
                .name("project-4")
                .storageSize(BigInteger.valueOf(1_000_000))
                .maxStorageSize(BigInteger.valueOf(2_000_000))
                .resources(new ArrayList<>())
                .build();
        Resource resource = Resource.builder()
                .id(1L)
                .name("TestName")
                .key("4project-4/TestName")
                .size(BigInteger.valueOf(1_000))
                .allowedRoles(new ArrayList<>(List.of(TeamRole.MANAGER)))
                .type(ResourceType.IMAGE)
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .project(project)
                .build();
        ResponseResourceDto responseResourceDto = ResponseResourceDto.builder()
                .id(1L)
                .key("4project-4/TestName")
                .build();

        when(teamMemberService.getTeamMemberByUserIdAndProjectId(1L, 1L)).thenReturn(teamMember);
        when(projectService.getProject(1L)).thenReturn(project);
        doNothing().when(resourceValidator).checkProjectStorageSizeExceeded(any(), any());
        doNothing().when(s3Util).s3UploadFile(any(), any());
        when(multipartFile.getOriginalFilename()).thenReturn("TestName");
        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getSize()).thenReturn(1_000L);
        when(resourceRepository.saveAll(anyList())).thenReturn(List.of(new Resource()));
        doNothing().when(projectService).saveProject(project);
        when(resourceMapper.toDtoFromEntity(any())).thenReturn(responseResourceDto);

        List<ResponseResourceDto> result = resourceService.uploadResources(List.of(multipartFile), 1L, 1L);
        verify(s3Util).s3UploadFile(multipartFile, resource.getKey());
        verify(resourceRepository).saveAll(anyList());
        verify(projectService).saveProject(any());

        assertEquals(1, result.size());
        assertTrue(result.contains(responseResourceDto));
    }

    @Test
    void deleteResource_Positive() {
        RequestDeleteResourceDto requestDto = RequestDeleteResourceDto.builder()
                .id(1L)
                .build();
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .roles(new ArrayList<>(List.of(TeamRole.MANAGER)))
                .build();
        Project project = Project.builder()
                .id(4L)
                .name("project-4")
                .storageSize(BigInteger.valueOf(1_001_000))
                .maxStorageSize(BigInteger.valueOf(2_000_000))
                .resources(new ArrayList<>())
                .build();
        Resource resource = createResource(project, teamMember);
        Resource resourceDeleted = createResource(project, teamMember);
        resourceDeleted.setStatus(ResourceStatus.DELETED);
        resourceDeleted.setKey(null);
        resourceDeleted.setSize(null);
        resourceDeleted.setUpdatedBy(teamMember);
        resourceDeleted.setUpdatedAt(LocalDateTime.now());

        when(resourceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(resource));
        doNothing().when(resourceValidator).checkUserInProject(anyLong(), any(), any());
        doNothing().when(s3Util).s3DeleteFile(anyString());
        doNothing().when(projectService).saveProject(project);
        when(resourceRepository.save(any())).thenReturn(resourceDeleted);

        resourceService.deleteResource(1L, requestDto);

        verify(resourceRepository).findById(anyLong());
        verify(resourceValidator).checkUserInProject(anyLong(), any(), any());
        verify(s3Util).s3DeleteFile(anyString());
        verify(resourceRepository).save(any());
        verify(projectService).saveProject(any());

        assertEquals(ResourceStatus.DELETED, resource.getStatus());
    }

    @Test
    void getResource() {
        Resource resource = Resource.builder()
                .id(1L)
                .name("TestName")
                .key("4project-4/TestName")
                .size(BigInteger.valueOf(1_000))
                .allowedRoles(new ArrayList<>(List.of(TeamRole.MANAGER)))
                .type(ResourceType.IMAGE)
                .status(ResourceStatus.ACTIVE)
                .build();
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        Resource result = resourceService.getResource(1L);
        assertEquals(resource, result);
    }

    @Test
    void updateResource() {
    }

    private static Resource createResource(Project project, TeamMember teamMember) {
        return Resource.builder()
                .id(1L)
                .name("TestName")
                .key("4project-4/TestName")
                .size(BigInteger.valueOf(1_000))
                .allowedRoles(new ArrayList<>(List.of(TeamRole.MANAGER)))
                .type(ResourceType.IMAGE)
                .status(ResourceStatus.ACTIVE)
                .project(project)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .build();
    }

    //        Project updateProject = Project.builder()
//                .id(4L)
//                .name("project-4")
//                .storageSize(BigInteger.valueOf(1_001_000))
//                .maxStorageSize(BigInteger.valueOf(2_000_000))
//                .build();
}