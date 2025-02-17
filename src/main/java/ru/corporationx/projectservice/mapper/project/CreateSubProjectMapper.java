package ru.corporationx.projectservice.mapper.project;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.corporationx.projectservice.model.dto.project.CreateSubProjectDto;
import ru.corporationx.projectservice.model.entity.Project;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreateSubProjectMapper {

    @Mapping(target = "children", ignore = true)
    Project toEntity(CreateSubProjectDto createSubProjectDto);

    @Mapping(target = "childrenIds", source = "children", qualifiedByName = "getChildrenIds")
    @Mapping(target = "parentId", source = "parentProject.id")
    CreateSubProjectDto toDto(Project project);

    @Named("getChildrenIds")
    default List<Long> getChildrenIds(List<Project> projects) {
        return projects.stream().map(Project::getId).toList();
    }
}
