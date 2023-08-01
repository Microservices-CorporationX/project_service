package faang.school.projectservice.mapper.subproject;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface SupProjectMapper {
    @Mapping(source = "parentProject.id", target = "parentProjectId")
    CreateProjectDto toDto(Project project);

    @Mapping(source = "parentProjectId", target = "parentProject.id")
    Project toEntity(CreateProjectDto createProjectDto);
}
