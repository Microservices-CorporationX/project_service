package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {
    @Mapping(source = "projects", target = "projectsIds", qualifiedByName = "mapToProjectIds")
    MomentDto toDto(Moment moment);

    @Mapping(target = "projects", ignore = true)
    Moment toEntity(MomentDto momentDto);

    @Named("mapToProjectIds")
    default List<Long> mapToProjectIds(List<Project> projects) {
        return mapToIds(projects, Project::getId);
    }

    default <T> List<Long> mapToIds(List<T> items, Function<T, Long> mapper) {
        if (items == null) {
            return new ArrayList<>();
        }
        return items.stream().map(mapper).toList();
    }
}
