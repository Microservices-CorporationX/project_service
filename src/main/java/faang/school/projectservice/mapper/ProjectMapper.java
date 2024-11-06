package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "ownerId", target = "ownerId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "visibility", target = "visibility")
    Project toEntity(CreateSubProjectDto createSubProjectDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "ownerId", target = "ownerId")
    @Mapping(source = "parentProject.id", target = "parentId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "visibility", target = "visibility")
    ProjectDto toDto(Project project);
}
