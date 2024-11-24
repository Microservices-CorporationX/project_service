package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectCreateResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateResponseDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectDto toDto(Project project);

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    ProjectCreateResponseDto toCreateResponseDto(Project project);

    @Mapping(source = "moments", target = "momentIds", qualifiedByName = "mapToMomentIds")
    ProjectUpdateResponseDto toUpdateResponseDto(Project project);

    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Mapping(target = "parentProject", ignore = true)
    Project toEntity(CreateProjectDto projectDto);

    @Named("mapToMomentIds")
    default List<Long> mapToMomentIds(List<Moment> moments) {
        return moments == null ? new ArrayList<>() : moments.stream().map(Moment::getId).toList();
    }
}
