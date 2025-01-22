package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipReadDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipReadMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "interns", target = "internsIds", qualifiedByName = "mapToIds")
    InternshipReadDto toDto(Internship internship);

    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "internsIds", ignore = true)
    Internship toEntity(InternshipReadDto internshipDto);

    @Named("mapToIds")
    default List<Long> mapToIds(List<TeamMember> interns) {
        return interns.stream().map(TeamMember::getId).toList();
    }

    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "internsIds", ignore = true)
    void update(@MappingTarget Internship entity, InternshipReadDto dto);
}
