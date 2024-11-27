package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

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
