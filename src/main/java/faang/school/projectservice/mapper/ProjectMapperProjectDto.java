package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.project.ProjectDto;

import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapperProjectDto {

    ProjectDto toDto(Project project);
    Project toEntity(ProjectDto projectDto);

    List<ProjectDto> toDtoList(List<Project> projectList);

    List<Project> toEntityList(List<ProjectDto> projectDtoList);
}
