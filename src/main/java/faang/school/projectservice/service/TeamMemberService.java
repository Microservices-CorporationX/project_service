package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember getTeamMemberByUserId(Long userId) {
        return teamMemberRepository.findById(userId);
    }

    public boolean existsById(Long userId) {
        return teamMemberRepository.existsById(userId);
    }
}
