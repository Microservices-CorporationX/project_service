package faang.school.projectservice.service.teammember;

import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamMemberService {
    private final TeamMemberRepository teamMemberJpaRepository;

    public TeamMember validateUserIsProjectMember(long userId, long projectId) {
        TeamMember teamMember = teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);
        if (teamMember == null) {
            log.info("User with id {} is not a member of project with id {}, access denied", userId, projectId);
            throw new AccessDeniedException("Not a member of this project");
        }
        return teamMember;
    }
}
