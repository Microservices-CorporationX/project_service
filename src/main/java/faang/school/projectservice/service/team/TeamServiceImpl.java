package faang.school.projectservice.service.team;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamMemberJpaRepository teamMemberRepository;

    @Override
    public void deleteMemberByUserId(Long userId) {
        teamMemberRepository.deleteByUserId(userId);
    }

    public Optional<TeamMember> findMemberByUserIdAndProjectId(Long userId, Long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
    }
}
