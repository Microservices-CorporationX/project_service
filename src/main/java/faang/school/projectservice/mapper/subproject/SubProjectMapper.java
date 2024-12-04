package faang.school.projectservice.mapper.subproject;

import faang.school.projectservice.dto.subprojectDto.subprojectDto.CreateSubProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.SubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubProjectMapper {
    @Mapping(target = "children", source = "children", qualifiedByName = "childrenMap")
    SubProjectDto toDto(Project project);
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "children", source = "children", qualifiedByName = "mapChildrenToEntities")
    @Mapping(target = "id", source = "parentID")
    Project toEntity(CreateSubProjectDto createSubProjectDto);

    @Named("childrenMap")
    default List<Long> mapChildren(List<Project> children) {
        if (children != null) {
            return children.stream().map(Project::getId).toList();
        }
        return null;
    }

    @Named("mapChildrenToEntities")
    default List<Project> mapChildrenToEntities(List<Long> childrenIds) {
        if (childrenIds == null) return null;
        return childrenIds.stream()
                .map(id -> {
                    Project project = new Project();
                    project.setId(id);
                    return project;
                })
                .toList();
    }
}
