package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "mentorId", source = "mentorId.id")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "internIds", source = "interns", qualifiedByName = "mapInternsToIds")
    InternshipDto toInternshipDto(Internship internship);

    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "interns", ignore = true)
    @Mapping(target = "project.id", source = "projectId")
    Internship toInternship(InternshipDto dto);

    List<Internship> toInternships(List<InternshipDto> dtos);

    List<InternshipDto> toInternshipDtos(List<Internship> entities);

    @Named("mapInternsToIds")
    default List<Long> mapInternsToIds(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getId)
                .toList();
    }
}
