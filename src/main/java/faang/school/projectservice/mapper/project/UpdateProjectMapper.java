package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.Project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateProjectMapper {
    ProjectUpdateDto toDto(Project project);
    Project toEntity(ProjectUpdateDto dto);
}
