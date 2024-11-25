package faang.school.projectservice.service.team;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    @Transactional
    public Team save(Team team) {
        return teamRepository.save(team);
    }

    public Team getById(Long id) {
        return teamRepository.getById(id);
    }

    public List<Team> findAllById(List<Long> ids) {
        return teamRepository.findAllById(ids);
    }
}
