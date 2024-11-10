package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.internShip.InternshipCreatedDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectServiceValidator projectServiceValidator;

    public void getProjectTeamMembersIds(InternshipCreatedDto internShipCreatedDto) {
        Long projectId = internShipCreatedDto.getProjectId();
        Project projectById = projectRepository.getProjectById(projectId);
        List<Team> teams = projectById.getTeams();
        List<Long> teamMembersId = teams.stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getId)
                .toList();

        TeamMember mentorId = internShipCreatedDto.getMentorId();

        if (!projectServiceValidator.isMentorPresent(teamMembersId, mentorId.getId())) {
            throw new IllegalArgumentException("Mentor is not present in project team");
        }
    }
}
