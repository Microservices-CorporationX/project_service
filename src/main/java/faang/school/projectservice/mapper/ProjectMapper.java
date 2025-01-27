package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    Project toProjectEntity(ProjectRequestDto projectRequestDto);
    Project toProjectEntity(ProjectResponseDto projectRequestDto);
    ProjectRequestDto toProjectRequestDto (Project project);
    ProjectResponseDto toProjectResponseDto (Project project);
    List<ProjectRequestDto> toProjectRequestDtos(List<Project> projects);
    List<ProjectRequestDto> toProjectResponseDtos(List<Project> projects);
}
