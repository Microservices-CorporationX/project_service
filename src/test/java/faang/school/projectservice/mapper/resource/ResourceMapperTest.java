package faang.school.projectservice.mapper.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.dto.resource.ResourceDtoStored;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceMapperTest {

    private final ResourceMapper resourceMapper = Mappers.getMapper(ResourceMapper.class);

    private final long resourceId = 1L;
    private final long teamMemberId = 2L;
    private final long projectId = 3L;

    private final String resourceName = "Resource name";
    private final BigInteger size = BigInteger.valueOf(1_000_000);
    private final String key = "";

    @Test
    public void toResourceDtoStoredSuccessTest() {
        LocalDateTime now = LocalDateTime.now();
        TeamMember teamMember = getTeamMember();
        Project project = getProject();

        List<TeamRole> allowedRoles = List.of(TeamRole.OWNER);
        Resource resource = Resource.builder()
                .id(resourceId)
                .name(resourceName)
                .size(size)
                .allowedRoles(allowedRoles)
                .type(ResourceType.PDF)
                .status(ResourceStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .updatedBy(teamMember)
                .createdBy(teamMember)
                .project(project)
                .build();

        ResourceDtoStored resourceDto = resourceMapper.toResourceDtoStored(resource);

        assertThat(resourceDto).isNotNull();
        assertThat(resourceDto.getId()).isEqualTo(resource.getId());
        assertThat(resourceDto.getName()).isEqualTo(resource.getName());
        assertThat(resourceDto.getFileBytes()).isNull();
        assertThat(resourceDto.getCreatedAt()).isEqualTo(resource.getCreatedAt());
        assertThat(resourceDto.getUpdatedAt()).isEqualTo(resource.getUpdatedAt());
        assertThat(resourceDto.getType()).isEqualTo(resource.getType());
        assertThat(resourceDto.getStatus()).isEqualTo(resource.getStatus());
        assertThat(resourceDto.getAllowedRoles()).isNotNull();
        assertThat(resourceDto.getAllowedRoles().size()).isEqualTo(1);
        assertThat(resourceDto.getAllowedRoles().get(0)).isEqualTo(resource.getAllowedRoles().get(0));
        assertThat(resourceDto.getCreatedById()).isEqualTo(resource.getCreatedBy().getId());
        assertThat(resourceDto.getUpdatedById()).isEqualTo(resource.getUpdatedBy().getId());
        assertThat(resourceDto.getProjectId()).isEqualTo(resource.getProject().getId());
    }

    @Test
    public void toDtoSuccessTest() {
        LocalDateTime now = LocalDateTime.now();
        TeamMember teamMember = getTeamMember();
        Project project = getProject();

        List<TeamRole> allowedRoles = List.of(TeamRole.OWNER);
        Resource resource = Resource.builder()
                .id(resourceId)
                .name(resourceName)
                .size(size)
                .allowedRoles(allowedRoles)
                .type(ResourceType.PDF)
                .status(ResourceStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .updatedBy(teamMember)
                .createdBy(teamMember)
                .project(project)
                .build();

        ResourceDto resourceDto = resourceMapper.toDto(resource);

        assertThat(resourceDto).isNotNull();
        assertThat(resourceDto.getId()).isEqualTo(resource.getId());
        assertThat(resourceDto.getName()).isEqualTo(resource.getName());
        assertThat(resourceDto.getKey()).isEqualTo(resource.getKey());
        assertThat(resourceDto.getCreatedAt()).isEqualTo(resource.getCreatedAt());
        assertThat(resourceDto.getUpdatedAt()).isEqualTo(resource.getUpdatedAt());
        assertThat(resourceDto.getType()).isEqualTo(resource.getType());
        assertThat(resourceDto.getStatus()).isEqualTo(resource.getStatus());
        assertThat(resourceDto.getAllowedRoles()).isNotNull();
        assertThat(resourceDto.getAllowedRoles().size()).isEqualTo(1);
        assertThat(resourceDto.getAllowedRoles().get(0)).isEqualTo(resource.getAllowedRoles().get(0));
        assertThat(resourceDto.getCreatedById()).isEqualTo(resource.getCreatedBy().getId());
        assertThat(resourceDto.getUpdatedById()).isEqualTo(resource.getUpdatedBy().getId());
        assertThat(resourceDto.getProjectId()).isEqualTo(resource.getProject().getId());
    }

    @Test
    public void toDtoWithoutCreatedByFailTest() {
        LocalDateTime now = LocalDateTime.now();
        Resource resource = Resource.builder()
                .id(resourceId)
                .name(resourceName)
                .size(size)
                .type(ResourceType.PDF)
                .status(ResourceStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        ResourceDto resourceDto = resourceMapper.toDto(resource);

        assertThat(resourceDto).isNotNull();
        assertThat(resourceDto.getId()).isEqualTo(resource.getId());
        assertThat(resourceDto.getName()).isEqualTo(resource.getName());
        assertThat(resourceDto.getKey()).isEqualTo(resource.getKey());
        assertThat(resourceDto.getCreatedAt()).isEqualTo(resource.getCreatedAt());
        assertThat(resourceDto.getUpdatedAt()).isEqualTo(resource.getUpdatedAt());
        assertThat(resourceDto.getType()).isEqualTo(resource.getType());
        assertThat(resourceDto.getStatus()).isEqualTo(resource.getStatus());
        assertThat(resourceDto.getAllowedRoles()).isNull();
        assertThat(resourceDto.getCreatedById()).isNull();
    }


    @Test
    public void toDtoWithoutUpdatedByFailTest() {
        LocalDateTime now = LocalDateTime.now();
        TeamMember teamMember = getTeamMember();

        Resource resource = Resource.builder()
                .id(resourceId)
                .name(resourceName)
                .size(size)
                .type(ResourceType.PDF)
                .status(ResourceStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .createdBy(teamMember)
                .build();

        ResourceDto resourceDto = resourceMapper.toDto(resource);

        assertThat(resourceDto).isNotNull();
        assertThat(resourceDto.getId()).isEqualTo(resource.getId());
        assertThat(resourceDto.getName()).isEqualTo(resource.getName());
        assertThat(resourceDto.getKey()).isEqualTo(resource.getKey());
        assertThat(resourceDto.getCreatedAt()).isEqualTo(resource.getCreatedAt());
        assertThat(resourceDto.getUpdatedAt()).isEqualTo(resource.getUpdatedAt());
        assertThat(resourceDto.getType()).isEqualTo(resource.getType());
        assertThat(resourceDto.getStatus()).isEqualTo(resource.getStatus());
        assertThat(resourceDto.getCreatedById()).isEqualTo(resource.getCreatedBy().getId());
        assertThat(resourceDto.getUpdatedById()).isNull();
    }

    @Test
    public void toDtoWithoutProjectFailTest() {
        LocalDateTime now = LocalDateTime.now();
        TeamMember teamMember = getTeamMember();

        Resource resource = Resource.builder()
                .id(resourceId)
                .name(resourceName)
                .size(size)
                .type(ResourceType.PDF)
                .status(ResourceStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .build();

        ResourceDto resourceDto = resourceMapper.toDto(resource);

        assertThat(resourceDto).isNotNull();
        assertThat(resourceDto.getId()).isEqualTo(resource.getId());
        assertThat(resourceDto.getName()).isEqualTo(resource.getName());
        assertThat(resourceDto.getKey()).isEqualTo(resource.getKey());
        assertThat(resourceDto.getCreatedAt()).isEqualTo(resource.getCreatedAt());
        assertThat(resourceDto.getUpdatedAt()).isEqualTo(resource.getUpdatedAt());
        assertThat(resourceDto.getType()).isEqualTo(resource.getType());
        assertThat(resourceDto.getStatus()).isEqualTo(resource.getStatus());
        assertThat(resourceDto.getCreatedById()).isEqualTo(resource.getCreatedBy().getId());
        assertThat(resourceDto.getUpdatedById()).isEqualTo(resource.getUpdatedBy().getId());
    }

    @Test
    public void toEntitySuccessTest() {
        ResourceDto resourceDto = getResourceDto();

        Resource resource = resourceMapper.toEntity(resourceDto);

        assertThat(resource).isNotNull();
        assertThat(resource.getId()).isEqualTo(resourceDto.getId());
        assertThat(resource.getName()).isEqualTo(resourceDto.getName());
        assertThat(resource.getKey()).isEqualTo(resourceDto.getKey());
        assertThat(resource.getType()).isEqualTo(resourceDto.getType());
        assertThat(resource.getStatus()).isEqualTo(resourceDto.getStatus());
        assertThat(resource.getSize()).isEqualTo(resourceDto.getSize());
        assertThat(resource.getAllowedRoles()).isNotNull();
        assertThat(resource.getAllowedRoles().size()).isEqualTo(resourceDto.getAllowedRoles().size());
        assertThat(resource.getAllowedRoles().get(0)).isEqualTo(resourceDto.getAllowedRoles().get(0));
        assertThat(resource.getCreatedAt()).isEqualTo(resourceDto.getCreatedAt());
        assertThat(resource.getUpdatedAt()).isEqualTo(resourceDto.getUpdatedAt());
        assertThat(resource.getCreatedBy().getId()).isEqualTo(resourceDto.getCreatedById());
        assertThat(resource.getUpdatedBy().getId()).isEqualTo(resourceDto.getUpdatedById());
        assertThat(resource.getProject().getId()).isEqualTo(resourceDto.getProjectId());
    }

    @Test
    public void toEntityWithoutProjectIdFailTest() {
        ResourceDto resourceDto = getResourceDto();
        resourceDto.setProjectId(null);

        Resource resource = resourceMapper.toEntity(resourceDto);

        assertThat(resource).isNotNull();
        assertThat(resource.getId()).isEqualTo(resourceDto.getId());
        assertThat(resource.getName()).isEqualTo(resourceDto.getName());
        assertThat(resource.getKey()).isEqualTo(resourceDto.getKey());
        assertThat(resource.getType()).isEqualTo(resourceDto.getType());
        assertThat(resource.getStatus()).isEqualTo(resourceDto.getStatus());
        assertThat(resource.getSize()).isEqualTo(resourceDto.getSize());
        assertThat(resource.getAllowedRoles()).isNotNull();
        assertThat(resource.getAllowedRoles().size()).isEqualTo(resourceDto.getAllowedRoles().size());
        assertThat(resource.getAllowedRoles().get(0)).isEqualTo(resourceDto.getAllowedRoles().get(0));
        assertThat(resource.getCreatedAt()).isEqualTo(resourceDto.getCreatedAt());
        assertThat(resource.getUpdatedAt()).isEqualTo(resourceDto.getUpdatedAt());
        assertThat(resource.getCreatedBy().getId()).isEqualTo(resourceDto.getCreatedById());
        assertThat(resource.getUpdatedBy().getId()).isEqualTo(resourceDto.getUpdatedById());
        assertThat(resource.getProject().getId()).isNull();
    }

    @Test
    public void toEntityWithoutCreatedByIdFailTest() {
        ResourceDto resourceDto = getResourceDto();
        resourceDto.setProjectId(null);
        resourceDto.setCreatedById(null);

        Resource resource = resourceMapper.toEntity(resourceDto);

        assertThat(resource).isNotNull();
        assertThat(resource.getId()).isEqualTo(resourceDto.getId());
        assertThat(resource.getName()).isEqualTo(resourceDto.getName());
        assertThat(resource.getKey()).isEqualTo(resourceDto.getKey());
        assertThat(resource.getType()).isEqualTo(resourceDto.getType());
        assertThat(resource.getStatus()).isEqualTo(resourceDto.getStatus());
        assertThat(resource.getSize()).isEqualTo(resourceDto.getSize());
        assertThat(resource.getAllowedRoles()).isNotNull();
        assertThat(resource.getAllowedRoles().size()).isEqualTo(resourceDto.getAllowedRoles().size());
        assertThat(resource.getAllowedRoles().get(0)).isEqualTo(resourceDto.getAllowedRoles().get(0));
        assertThat(resource.getCreatedAt()).isEqualTo(resourceDto.getCreatedAt());
        assertThat(resource.getUpdatedAt()).isEqualTo(resourceDto.getUpdatedAt());
        assertThat(resource.getCreatedBy().getId()).isNull();
        assertThat(resource.getUpdatedBy().getId()).isEqualTo(resourceDto.getUpdatedById());
        assertThat(resource.getProject().getId()).isNull();
    }

    @Test
    public void toEntityWithoutCreatedByIdFailTest1() {
        ResourceDto resourceDto = getResourceDto();
        resourceDto.setProjectId(null);
        resourceDto.setCreatedById(null);

        Resource resource = resourceMapper.toEntity(resourceDto);

        assertThat(resource).isNotNull();
        assertThat(resource.getId()).isEqualTo(resourceDto.getId());
        assertThat(resource.getName()).isEqualTo(resourceDto.getName());
        assertThat(resource.getKey()).isEqualTo(resourceDto.getKey());
        assertThat(resource.getType()).isEqualTo(resourceDto.getType());
        assertThat(resource.getStatus()).isEqualTo(resourceDto.getStatus());
        assertThat(resource.getSize()).isEqualTo(resourceDto.getSize());
        assertThat(resource.getAllowedRoles()).isNotNull();
        assertThat(resource.getAllowedRoles().size()).isEqualTo(resourceDto.getAllowedRoles().size());
        assertThat(resource.getAllowedRoles().get(0)).isEqualTo(resourceDto.getAllowedRoles().get(0));
        assertThat(resource.getCreatedAt()).isEqualTo(resourceDto.getCreatedAt());
        assertThat(resource.getUpdatedAt()).isEqualTo(resourceDto.getUpdatedAt());
        assertThat(resource.getCreatedBy().getId()).isNull();
        assertThat(resource.getUpdatedBy().getId()).isEqualTo(resourceDto.getUpdatedById());
        assertThat(resource.getProject().getId()).isNull();
    }

    @Test
    public void toEntityAllUndefinedFailTest() {
        ResourceDto resourceDto = new ResourceDto();

        Resource resource = resourceMapper.toEntity(resourceDto);

        assertThat(resource.getId()).isNull();
        assertThat(resource.getName()).isNull();
        assertThat(resource.getKey()).isNull();
        assertThat(resource.getType()).isNull();
        assertThat(resource.getStatus()).isNull();
        assertThat(resource.getAllowedRoles()).isNull();
        assertThat(resource.getCreatedAt()).isNull();
        assertThat(resource.getUpdatedAt()).isNull();
        assertThat(resource.getCreatedBy().getId()).isNull();
        assertThat(resource.getUpdatedBy().getId()).isNull();
        assertThat(resource.getProject().getId()).isNull();
    }

    private TeamMember getTeamMember() {
        return TeamMember.builder()
                .id(teamMemberId)
                .build();
    }

    private Project getProject() {
        return Project.builder()
                .id(projectId)
                .build();
    }

    private ResourceDto getResourceDto() {

        LocalDateTime now = LocalDateTime.now();
        List<TeamRole> allowedRoles = List.of(TeamRole.OWNER);
        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setId(resourceId);
        resourceDto.setName(resourceName);
        resourceDto.setKey(key);
        resourceDto.setSize(size);
        resourceDto.setType(ResourceType.PDF);
        resourceDto.setStatus(ResourceStatus.ACTIVE);
        resourceDto.setAllowedRoles(allowedRoles);
        resourceDto.setCreatedAt(now);
        resourceDto.setUpdatedAt(now);
        resourceDto.setCreatedById(teamMemberId);
        resourceDto.setUpdatedById(teamMemberId);
        resourceDto.setProjectId(projectId);
        return resourceDto;
    }
//
//    private ResourceDtoStored getResourceDtoStored() {
//        LocalDateTime now = LocalDateTime.now();
//        byte[] bytes = new byte[] {1, 2, 3, 4, 5, 6};
//
//        List<TeamRole> allowedRoles = List.of(TeamRole.OWNER);
//        ResourceDtoStored resourceDto = new ResourceDtoStored();
//        resourceDto.setId(resourceId);
//        resourceDto.setName(resourceName);
////        resourceDto.setKey(key);
//        resourceDto.setSize(size);
//        resourceDto.setType(ResourceType.PDF);
//        resourceDto.setStatus(ResourceStatus.ACTIVE);
//        resourceDto.setAllowedRoles(allowedRoles);
//        resourceDto.setCreatedAt(now);
//        resourceDto.setUpdatedAt(now);
//        resourceDto.setFileBytes();
//        resourceDto.setCreatedById(teamMemberId);
//        resourceDto.setUpdatedById(teamMemberId);
//        resourceDto.setProjectId(projectId);
//
//        return resourceDto;
//    }
}
