package faang.school.projectservice.service.team;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    @Transactional
    public Team save(Team team) {
        return teamRepository.save(team);
    }
}
