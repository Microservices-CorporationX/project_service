package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "parentProjectId", source = "parentProject.id")
    @Mapping(target = "childrenIds", source = "children", qualifiedByName = "mapChildrenToIds")
    ProjectDto toDto(Project project);

    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "teams", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "moments", ignore = true)
    @Mapping(target = "meets", ignore = true)
    @Mapping(target = "galleryFileKeys", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Mapping(target = "parentProject.id", source = "parentProjectId")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "teams", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "moments", ignore = true)
    @Mapping(target = "meets", ignore = true)
    @Mapping(target = "galleryFileKeys", ignore = true)
    Project toEntity(CreateSubProjectDto createSubProjectDto);

    List<ProjectDto> toDtoList(List<Project> projects);

    @Named("mapChildrenToIds")
    default List<Long> mapChildrenToIds(List<Project> children) {
        return children != null ? children.stream().map(Project::getId).toList() : List.of();
    }
}