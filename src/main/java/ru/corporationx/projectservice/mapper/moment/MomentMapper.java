package ru.corporationx.projectservice.mapper.moment;

import org.mapstruct.*;
import ru.corporationx.projectservice.model.dto.moment.MomentDto;
import ru.corporationx.projectservice.model.entity.Moment;
import ru.corporationx.projectservice.model.entity.Project;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    @Mapping(target = "projects", ignore = true)
    Moment toEntity(MomentDto momentDto);

    @Mapping(target = "projectIds", source = "projects", qualifiedByName = "getProjectIds")
    MomentDto toDto(Moment moment);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Moment moment, MomentDto momentDto);

    @Named("getProjectIds")
    default List<Long> getProjectIds(List<Project> projects) {
        return projects.stream().map(Project::getId).toList();
    }
}
