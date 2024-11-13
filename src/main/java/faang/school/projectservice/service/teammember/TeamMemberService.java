package faang.school.projectservice.service.teammember;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public Optional<TeamMember> findTeamMember(Long id){
        if (id == null){
            throw new IllegalArgumentException("Project not found");
        }

        return Optional.ofNullable(teamMemberRepository.findById(id));
    }
}
