package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface MomentMapper {

    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "projectsToProjectIds")
    @Mapping(source = "userIds", target = "teamMemberIds")
    MomentDto toDto(Moment moment);

    @Mapping(target = "resource", ignore = true)
    @Mapping(source = "projectIds", target = "projects", qualifiedByName = "projectIdsToProjects")
    @Mapping(source = "teamMemberIds", target = "userIds")
    Moment toEntity(MomentDto momentDto);

    @Named("projectsToProjectIds")
    default List<Long> mapProjectsToProjectIds(List<Project> projects) {
        return projects != null ? projects.stream().map(Project::getId).collect(Collectors.toList()) : null;
    }

    @Named("projectIdsToProjects")
    default List<Project> mapProjectIdsToProjects(List<Long> projectIds) {
        return projectIds != null ? projectIds.stream().map(id -> {
            Project project = new Project();
            project.setId(id);
            return project;
        }).collect(Collectors.toList()) : null;
    }
}
