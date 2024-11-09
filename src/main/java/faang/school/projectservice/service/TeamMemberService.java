package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public boolean curatorHasNoAccess(Long curatorId) {
        TeamMember teamMember = teamMemberRepository.findById(curatorId);
        return !teamMember.isCurator();
    }
}
