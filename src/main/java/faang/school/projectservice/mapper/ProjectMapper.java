package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.CreateProjectRequest;
import faang.school.projectservice.dto.project.ProjectResponse;
import faang.school.projectservice.dto.project.UpdateProjectRequest;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProjectMapper {
    @Mapping(target = "visibility", defaultValue = "PUBLIC")
    Project toEntity(CreateProjectRequest createProjectRequest);

    ProjectResponse toProjectResponse(Project project);

    void updateProjectFromDto(UpdateProjectRequest updateProjectRequest, @MappingTarget Project project);
}