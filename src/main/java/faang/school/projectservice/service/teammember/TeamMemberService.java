package faang.school.projectservice.service.teammember;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamMemberService {

    private final TeamMemberJpaRepository teamMemberRepository;

    public TeamMember findById(Long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Team member doesn't exist by id: %d".formatted(id)));
    }

    public TeamMember save(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }

    public TeamMember getByUserIdAndProjectId(long userId, long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
    }
}
