package faang.school.projectservice.mapper.momentMapper;

import faang.school.projectservice.dto.momentDto.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface MomentMapper {
    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "map")
    MomentDto toDto(Moment moment);

    @Mapping(target = "projects", ignore = true)
    Moment toEntity(MomentDto momentDto);

    @Named("map")
    default List<Long> map(List<Project> moments) {
        return moments.stream().map(Project::getId).toList();
    }
}
