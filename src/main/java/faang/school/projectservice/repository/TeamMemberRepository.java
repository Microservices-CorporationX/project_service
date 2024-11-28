package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TeamMemberRepository {
    private final TeamMemberJpaRepository jpaRepository;

    public TeamMember findById(Long id) {
        return jpaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", id)));
    }

    public List<TeamMember> findAllByIds(List<Long> ids) {
        var result = jpaRepository.findAllById(ids);
        if (result.size() != ids.size()) {
            String notFoundIds = ids.stream()
                    .filter(id -> !result.stream()
                            .map(TeamMember::getId)
                            .toList()
                            .contains(id))
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new EntityNotFoundException(String.format("Team members doesn't exist by ids: %s", notFoundIds));
        }
        return result;
    }

    public TeamMember findByUserIdAndProjectId(long userId, long projectId) {
        var result = jpaRepository.findByUserIdAndProjectId(userId, projectId);
        if (result == null) {
            throw new EntityNotFoundException(
                    String.format("Team member doesn't exist by user id: %s and project id: %s", userId, projectId)
            );
        }
        return result;
    }
}