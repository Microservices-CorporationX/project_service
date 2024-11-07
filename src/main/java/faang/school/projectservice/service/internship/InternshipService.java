package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.client.internship.InternshipCreationDto;
import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.team.TeamService;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import faang.school.projectservice.validator.internship.InternshipDtoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipDtoValidator internshipDtoValidator;
    private final InternshipMapper internshipMapper;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final TeamService teamService;

    public InternshipDto createInternship(InternshipCreationDto internshipCreationDto) {
        TeamMember mentor = internshipDtoValidator.validateCreationDtoAndGetMentor(internshipCreationDto);

        Internship internship = internshipMapper.toEntity(internshipCreationDto);

        Team team = teamService.save(
                Team.builder()
                        .project(mentor.getTeam().getProject())
                        .build()
        );
        List<TeamMember> interns = createTeamMembersByUserIds(internshipCreationDto.getInternUserIds(), team, TeamRole.INTERN);

        internship.setInterns(interns);
        internship.setMentorId(mentor);
        internship.setProject(mentor.getTeam().getProject());
        internship.setStatus(InternshipStatus.IN_PROGRESS);

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    private List<TeamMember> createTeamMembersByUserIds(List<Long> userIds, Team team, TeamRole role) {
        return userIds.stream()
                .map(userId -> teamMemberService.save(
                        TeamMember.builder()
                                .userId(userId)
                                .roles(List.of(role))
                                .team(team)
                                .build()
                )).toList();
    }
}
