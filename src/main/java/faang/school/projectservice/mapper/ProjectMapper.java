package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectReadDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(target = "status", defaultValue = "CREATED")
    Project toEntity(ProjectCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProjectUpdateDto dto, @MappingTarget Project entity);

    ProjectReadDto toProjectDto(Project project);

    @AfterMapping
    default void setUpdatedAt(@MappingTarget Project entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }
}
