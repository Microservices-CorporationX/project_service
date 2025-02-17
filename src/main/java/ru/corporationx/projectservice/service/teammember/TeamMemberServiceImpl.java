package ru.corporationx.projectservice.service.teammember;

import ru.corporationx.projectservice.exception.AccessDeniedException;
import ru.corporationx.projectservice.exception.DataValidationException;
import ru.corporationx.projectservice.model.entity.TeamMember;
import ru.corporationx.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public TeamMember findById(Long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("Team member doesn't exist by id: %s", id)));
    }

    @Override
    public TeamMember validateUserIsProjectMember(long userId, long projectId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        if (teamMember == null) {
            log.info("User with id {} is not a member of project with id {}, access denied", userId, projectId);
            throw new AccessDeniedException("Not a member of this project");
        }
        return teamMember;
    }
}
