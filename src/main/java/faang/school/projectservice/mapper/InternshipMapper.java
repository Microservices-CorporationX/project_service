package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.internShip.InternshipCreatedDto;
import faang.school.projectservice.dto.internShip.InternshipFilterDto;
import faang.school.projectservice.dto.internShip.InternshipGetAllDto;
import faang.school.projectservice.dto.internShip.InternshipGetByIdDto;
import faang.school.projectservice.dto.internShip.InternshipUpdatedDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(source = "projectId", target = "project")
    @Mapping(source = "mentorId", target = "mentorId")
    Internship createInternship(InternshipCreatedDto dto);

    @Mapping(source = "interns", target = "interns")
    Internship toEntity(InternshipUpdatedDto dto);

    InternshipUpdatedDto toUpdatedDto(Internship entity);

    InternshipGetAllDto toGetAllDto(Internship internship);

    InternshipGetByIdDto toGetByIdDto(Internship internship);

    InternshipFilterDto toFilterDto(Internship internship);

    InternshipFilterDto toFilterDto(TeamMember teamMember);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    InternshipCreatedDto toCreatedDto(Internship entity);

    default Project mapProject(Long projectId) {
        if (projectId == null) return null;
        Project project = new Project();
        project.setId(projectId);
        return project;
    }

    default TeamMember mapMentor(Long mentorId) {
        if (mentorId == null) return null;
        TeamMember mentor = new TeamMember();
        mentor.setId(mentorId);
        return mentor;
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

    default List<Long> mapTeamMembersToIds(List<TeamMember> teamMembers) {
        return teamMembers != null ? teamMembers.stream()
                .map(TeamMember::getId)
                .collect(Collectors.toList()) : null;
    }
}
