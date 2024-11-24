package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProjectMapper {

    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "visibility", expression = "java(projectDto.getVisibility() != null ? projectDto.getVisibility() : faang.school.projectservice.model.ProjectVisibility.PUBLIC)")
    Project toEntityCreate(ProjectDto projectDto);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "description", source = "description")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(ProjectDto projectDto, @MappingTarget Project existingProject);

    ProjectDto toDto(Project project);
}
