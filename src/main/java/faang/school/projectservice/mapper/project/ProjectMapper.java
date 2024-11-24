package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectCreateResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateResponseDto;
import faang.school.projectservice.model.Project;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectDto toDto(Project project);

    ProjectCreateResponseDto toCreateResponseDto(Project project);

    ProjectUpdateResponseDto toUpdateResponseDto(Project project);

    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Mapping(target = "parentProject", ignore = true)
    Project toEntity(CreateProjectDto projectDto);
}
