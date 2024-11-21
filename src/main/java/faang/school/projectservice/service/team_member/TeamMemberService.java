package faang.school.projectservice.service.team_member;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberJpaRepository teamMemberRepository;

    public List<TeamMember> findAllById(List<Long> id) {
        return teamMemberRepository.findAllById(id);
    }
}
