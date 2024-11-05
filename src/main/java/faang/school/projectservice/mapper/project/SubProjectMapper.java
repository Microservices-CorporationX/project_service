package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface SubProjectMapper {
    @Mapping(target = "id", ignore = true)
    Project toEntity(ProjectDto projectDto);

    CreateSubProjectDto toDto(Project project);

}
