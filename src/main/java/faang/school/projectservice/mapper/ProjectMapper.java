package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    Project toProjectEntity(ProjectDto projectDto);

    ProjectDto toProjectDto (Project project);

    List<Project> toProjectEntities(List<ProjectDto> projectDtos);

    List<ProjectDto> toProjectDtos(List<Project> projects);
}
