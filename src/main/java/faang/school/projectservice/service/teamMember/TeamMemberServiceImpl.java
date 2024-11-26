package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public TeamMember findById(Long id) {
        return teamMemberRepository.findById(id);
    }

    @Override
    public TeamMember save(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }
}
