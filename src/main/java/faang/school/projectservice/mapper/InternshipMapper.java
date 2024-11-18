package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(source = "mentorId", target = "mentorId", qualifiedByName = "mapTeamMemberToLong")
    InternshipDto toDto(Internship internship);

    @Mapping(source = "mentorId", target = "mentorId", qualifiedByName = "mapLongToTeamMember")
    Internship toEntity(InternshipDto internshipDto);

    List<Internship> toEntities(List<InternshipDto> internshipDtos);

    List<InternshipDto> toDtos(List<Internship> internships);

    @Named("mapTeamMemberToLong")
    default Long mapTeamMemberToLong(TeamMember teamMember) {
        return teamMember.getId();
    }

    @Named("mapLongToTeamMember")
    default TeamMember mapLongToTeamMember(Long teamMemberId) {
        return TeamMember.builder().id(teamMemberId).build();
    }
}
