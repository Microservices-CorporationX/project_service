package faang.school.projectservice.service.team;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.TeamService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;

    @Override
    public Team createTeam(List<TeamMember> members, Project project) {
        return teamRepository.save(
                Team.builder()
                        .teamMembers(members)
                        .project(project)
                        .build()
        );
    }
}
