package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberJpaRepository teamMemberRepository;

    public TeamMember save(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }

    public TeamMember getByUserIdAndProjectId(long userId, long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
    }
}
