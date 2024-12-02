package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TeamMemberRepository {
    private final TeamMemberJpaRepository jpaRepository;

    public TeamMember findById(Long id) {
        return jpaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", id)));
    }

    public List<TeamMember> findAllByIds(List<Long> ids) {
        List<TeamMember> teamMembers = jpaRepository.findAllById(ids);
        if (teamMembers.isEmpty()) {
            throw new EntityNotFoundException(String.format("Team members doesn't exist by ids: %s", ids));
        }

        return teamMembers;
    }

    public void saveAll(List<TeamMember> teamMembers) {
        jpaRepository.saveAll(teamMembers);
    }

    public void save(TeamMember teamMember) {
        jpaRepository.save(teamMember);
    }

    public Optional<TeamMember> findByUserIdAndProjectId(Long userId, Long projectId) {
        return jpaRepository.findByUserIdAndProjectId(userId, projectId);
    }

    public List<TeamMember> findAllMembersByProjectId(Long projectId) {
        return jpaRepository.findAllMembersByProjectId(projectId);
    }

    public void delete(TeamMember teamMember) {
        jpaRepository.delete(teamMember);
    }

    public void updateTeamMembers(Long teamId, List<TeamMember> teamMembers) {
        jpaRepository.updateTeamMembers(teamId, teamMembers);
    }
}
