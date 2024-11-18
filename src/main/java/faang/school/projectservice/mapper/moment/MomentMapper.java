package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.controller.moment.MomentController;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    Moment toEntity(MomentDto momentDto);

    @Mapping(target = "projectIds", source = "children", qualifiedByName = "getChildrenIds")
    MomentDto toDto(Moment moment);

    @Named("getProjectIds")
    default List<Long> getProjectIds(List<Project> projects) {
        return projects.stream().map(Project::getId).toList();
    }
}
