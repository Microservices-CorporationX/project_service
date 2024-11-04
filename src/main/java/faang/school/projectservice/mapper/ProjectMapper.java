package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto toDto(Project project);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "storageSize", ignore = true)
    @Mapping(target = "maxStorageSize", ignore = true)
    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "coverImageId", ignore = true)
    @Mapping(target = "teams", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "vacancies", ignore = true)
    @Mapping(target = "moments", ignore = true)
    @Mapping(target = "meets", ignore = true)
    Project mapToEntity(ProjectDto projectDto);
}