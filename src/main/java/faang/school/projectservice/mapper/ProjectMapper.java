package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "visibility", source = "visibility")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ProjectDto toDto(Project project);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "visibility", source = "visibility")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdAt", ignore = true)
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
}