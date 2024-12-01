package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.*;

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
