package faang.school.projectservice.mapper.momentMapper;

import faang.school.projectservice.dto.momentDto.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface MomentMapper {
    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "map")
    @Mapping(source = "date", target = "date", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "localDateTimeToString")
    MomentDto toDto(Moment moment);

    @Mapping(target = "projects", ignore = true)
    @Mapping(source = "date", target = "date", qualifiedByName = "stringToLocalDateTime")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "stringToLocalDateTime")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "stringToLocalDateTime")
    Moment toEntity(MomentDto momentDto);

    @Named("map")
    default List<Long> map(List<Project> moments) {
        return moments.stream().map(Project::getId).toList();
    }

    @Named("localDateTimeToString")
    default String mapDateToString(LocalDateTime date) {
        return date != null ? date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String date) {
        return date != null ? LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }
}
