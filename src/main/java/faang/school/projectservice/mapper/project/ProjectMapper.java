package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(target = "children", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Mapping(source = "children", target = "childrenIds", qualifiedByName = "getChildrenIds")
    ProjectDto toDto(Project project);

    @Named("getChildrenIds")
    default List<Long> getChildrenIds(List<Project> children) {
        return children.stream().map(Project::getId).toList();
    }
}
