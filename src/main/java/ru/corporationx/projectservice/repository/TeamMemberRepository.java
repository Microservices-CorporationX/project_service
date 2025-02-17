package ru.corporationx.projectservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.corporationx.projectservice.model.entity.TeamMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    Optional<TeamMember> findById(long id);

    Optional<List<TeamMember>> findAllByIdIn(List<Long> memberIds);

    void deleteById(long id);

    @Query(
            "SELECT tm FROM TeamMember tm JOIN tm.team t " +
                    "WHERE tm.userId = :userId " +
                    "AND t.project.id = :projectId"
    )
    TeamMember findByUserIdAndProjectId(long userId, long projectId);

    List<TeamMember> findByUserId(long userId);
}
