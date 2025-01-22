package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentCreateDto;
import faang.school.projectservice.dto.moment.MomentGetDto;
import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    @Mapping(target = "projects", ignore = true)
    Moment toEntity(MomentCreateDto dto);

    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "mapToIds")
    MomentCreateDto toDto(Moment entity);

    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "mapToIds")
    MomentGetDto toGetDto(Moment entity);

    @Named("mapToIds")
    default List<Long> map(List<Project> projectsFromEntity) {
        return projectsFromEntity.stream().map(Project::getId).toList();
    }
}
