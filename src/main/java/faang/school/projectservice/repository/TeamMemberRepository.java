package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamMemberRepository {

    private final TeamMemberJpaRepository jpaRepository;

    public TeamMember findById(Long id) {
        return jpaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", id)));
    }

    public boolean isUserInAnyTeamOfProject(long projectId, long userId) {
        return jpaRepository.isUserInAnyTeamOfProject(projectId, userId);
    }

    public long countUsersInProjectTeams(long projectId, List<Long> userIds) {
        return jpaRepository.countUsersInProjectTeams(projectId, userIds);
    }
}
