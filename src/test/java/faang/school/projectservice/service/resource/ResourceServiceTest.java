package faang.school.projectservice.service.resource;

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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @InjectMocks
    private  ResourceService resourceService;

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
        when(resourceMapper.toDtoFromEntity(any())).thenReturn(responseResourceDto);

        List<ResponseResourceDto> result = resourceService.uploadResources(List.of(multipartFile), 1L, 1L);
        verify(s3Util).s3UploadFile(multipartFile, resource.getKey());
        verify(resourceRepository).saveAll(anyList());
        verify(projectService).saveProject(any());

        assertEquals(1, result.size());
        assertTrue(result.contains(responseResourceDto));
    }

    @Test
    void deleteResource() {
    }

    @Test
    void getResource() {
    }

    @Test
    void updateResource() {
    }

    //        Project updateProject = Project.builder()
//                .id(4L)
//                .name("project-4")
//                .storageSize(BigInteger.valueOf(1_001_000))
//                .maxStorageSize(BigInteger.valueOf(2_000_000))
//                .build();
}