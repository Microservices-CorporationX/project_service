package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "status", expression = "java(mapProjectStatus())")
    @Mapping(target = "createdAt", expression = "java(mapTime())")
    @Mapping(target = "updatedAt", expression = "java(mapTime())")
    Project toEntity(CreateSubProjectDto createSubProjectDto);

    ProjectDto toDto(Project project);

    List<ProjectDto> toDto(List<Project> projects);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UpdateSubProjectDto updateSubProjectDto, @MappingTarget Project project);

    default ProjectStatus mapProjectStatus() {
        return ProjectStatus.CREATED;
    }

    default LocalDateTime mapTime() {
        return LocalDateTime.now();
    }
}
