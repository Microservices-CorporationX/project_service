package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "interns", target = "internIds", qualifiedByName = "internsMap")
    @Mapping(source = "mentorId", target = "mentorId", qualifiedByName = "mentorToLong")
    InternshipDto toDto(Internship internship);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "interns", ignore = true)
    @Mapping(source = "mentorId", target = "mentorId", qualifiedByName = "longToMentor")
    Internship toEntity(InternshipDto dto);

    void update(InternshipDto dto, @MappingTarget Internship internship);

    @Named("internsMap")
    default List<Long> internsMap(List<TeamMember> interns) {
        return interns.stream()
                .map(TeamMember::getId)
                .toList();
    }

    @Named("mentorToLong")
    default Long mentorToLong(TeamMember mentor) {
        return mentor != null ? mentor.getId() : null;
    }

    @Named("longToMentor")
    default TeamMember longToMentor(Long mentorId) {
        if (mentorId == null) {
            return null;
        }
        TeamMember mentor = new TeamMember();
        mentor.setId(mentorId);
        return mentor;
    }
}

