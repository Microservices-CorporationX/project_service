package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "visibility", expression = "java(projectDto.getVisibility() != null ? projectDto.getVisibility() : faang.school.projectservice.model.ProjectVisibility.PUBLIC)")
    Project toEntityCreate(ProjectDto projectDto);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "description", source = "description")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Project toEntityUpdate(ProjectDto projectDto);

    ProjectDto toDto(Project project);
}
