package faang.school.projectservice.service.team;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberJpaRepository teamMemberRepository;

    @Override
    public void deleteMemberByUserId(Long userId) {
        teamMemberRepository.deleteByUserId(userId);
    }

    @Override
    public Optional<Team> findTeamByProjectId(Long projectId) {
        return teamRepository.findByProjectId(projectId);
    }

    public Optional<TeamMember> findMemberByUserIdAndProjectId(Long userId, Long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
    }
}
