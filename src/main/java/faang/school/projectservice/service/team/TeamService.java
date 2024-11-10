package faang.school.projectservice.service.team;

import faang.school.projectservice.exception.EntityNullException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService {

    private static final String TEAM = "Team";

    private final TeamRepository teamRepository;

    public Team save(Team team) {
        if (team == null) {
            throw new EntityNullException(TEAM);
        }
        return teamRepository.save(team);
    }
}
