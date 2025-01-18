package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "parentProject.id", source = "parentProjectId")
    @Mapping(target = "projectVisibility", source = "projectVisibility")
    @Mapping(target = "createdAt", source = "createdAt")
    Project toEntity(CreateSubProjectDto createDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "parentProjectId", source = "parentProject.id")
    @Mapping(target = "projectVisibility", source = "projectVisibility")
    @Mapping(target = "createdAt", source = "createdAt")
    ProjectDto toDto(Project project);
}
