package faang.school.projectservice.service.team_member;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberJpaRepository teamMemberRepository;

    public TeamMember getTeamMemberEntity(long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException("TeamMember", teamMemberId));
    }
}
