package ru.corporationx.projectservice.mapper.project;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.corporationx.projectservice.model.dto.project.ProjectDto;
import ru.corporationx.projectservice.model.dto.project.ProjectEvent;
import ru.corporationx.projectservice.model.entity.Project;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(target = "children", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Mapping(source = "children", target = "childrenIds", qualifiedByName = "getChildrenIds")
    ProjectDto toDto(Project project);

    @Mapping(target = "projectId", source = "id")
    ProjectEvent toEvent(Project project);

    @Named("getChildrenIds")
    default List<Long> getChildrenIds(List<Project> children) {
        return children.stream().map(Project::getId).toList();
    }
}
