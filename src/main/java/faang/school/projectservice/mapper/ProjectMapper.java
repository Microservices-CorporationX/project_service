package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "parentProject.id", source = "parentProjectId")
    @Mapping(target = "visibility", source = "visibility")
    Project toEntity(CreateSubProjectDto createDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "parentProjectId", source = "parentProject.id")
    @Mapping(target = "visibility", source = "visibility")
    @Mapping(target = "createdAt", source = "createdAt")
    ProjectDto toDto(Project project);

    Project toUpdatedEntity(UpdateSubProjectDto updateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateSubProjectDto updateDto, @MappingTarget Project project);

}
