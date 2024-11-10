package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.internShip.InternShipUpdatedDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternShipUpdateMapper {
    @Mapping(source = "projectId", target = "project")
    @Mapping(source = "mentorId", target = "mentorId")
    @Mapping(source = "interns", target = "interns")
    Internship toEntity(InternShipUpdatedDto dto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "interns", target = "interns")
    InternShipUpdatedDto toDto(Internship entity);

    default Project mapProject(Long projectId) {
        if (projectId == null) {
            return null;
        }
        Project project = new Project();
        project.setId(projectId);
        return project;
    }

    default TeamMember mapMentor(Long mentorId) {
        if (mentorId == null) {
            return null;
        }
        TeamMember mentor = new TeamMember();
        mentor.setId(mentorId);
        return mentor;
    }

    default List<Long> mapTeamMembersToIds(List<TeamMember> teamMembers) {
        return teamMembers != null ? teamMembers.stream()
                .map(TeamMember::getId)
                .collect(Collectors.toList()) : null;
    }

    default List<TeamMember> mapInternIdsToTeamMembers(List<Long> internIds) {
        return internIds != null ? internIds.stream()
                .map(id -> {
                    TeamMember intern = new TeamMember();
                    intern.setId(id);
                    return intern;
                })
                .collect(Collectors.toList()) : null;
    }
}
