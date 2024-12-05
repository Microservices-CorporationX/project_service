package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "updatedBy.id", target = "updatedById")
    @Mapping(source = "project.id", target = "projectId")
    ResourceDto toDto(Resource resource);

    @Mapping(target = "createdBy", expression = "java(mapCreatedById(resourceDto.getCreatedById()))")
    @Mapping(target = "updatedBy", expression = "java(mapUpdatedById(resourceDto.getUpdatedById()))")
    @Mapping(target = "project", expression = "java(mapProjectId(resourceDto.getProjectId()))")
    Resource toEntity(ResourceDto resourceDto);

    default TeamMember mapCreatedById(Long createdById) {
        return TeamMember.builder()
                .id(createdById)
                .build();
    }

    default TeamMember mapUpdatedById(Long updatedById) {
        return TeamMember.builder()
                .id(updatedById)
                .build();
    }

    default Project mapProjectId(Long projectId) {
        return Project.builder()
                .id(projectId)
                .build();
    }
}
