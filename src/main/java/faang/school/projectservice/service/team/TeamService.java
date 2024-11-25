package faang.school.projectservice.service.team;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public Team findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Huid: " + id));
    }

    public List<Team> findAllById(List<Long> ids) {
        return teamRepository.findAllById(ids);
    }
}
