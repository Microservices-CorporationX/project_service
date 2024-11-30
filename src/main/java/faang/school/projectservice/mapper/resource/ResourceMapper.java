package faang.school.projectservice.mapper.resource;

import faang.school.projectservice.dto.client.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "project", ignore = true)
    Resource toEntity(ResourceDto resourceDto);

    @Mapping(target = "createdById", source = "createdBy", qualifiedByName = "getTeamMemberId")
    @Mapping(target = "projectId", source = "project", qualifiedByName = "getProjectId")
    ResourceDto toDto(Resource resource);

    @Named("getTeamMemberId")
    default long getTeamMemberId(TeamMember teamMember){
        return teamMember.getId();
    }

    @Named("getProjectId")
    default long getProjectId(Project project){
        return project.getId();
    }
}
