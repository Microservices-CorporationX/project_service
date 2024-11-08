package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateProjectMapper {

    UpdateProjectDto toDto(Project project);

    Project toEntity(UpdateProjectDto dto);
}
