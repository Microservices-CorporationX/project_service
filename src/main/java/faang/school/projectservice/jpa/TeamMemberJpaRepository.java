package faang.school.projectservice.jpa;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberJpaRepository extends JpaRepository<TeamMember, Long> {
    @Query(
        "SELECT tm FROM TeamMember tm JOIN tm.team t " +
        "WHERE tm.userId = :userId " +
        "AND t.project.id = :projectId"
    )
    TeamMember findByUserIdAndProjectId(long userId, long projectId);

    List<TeamMember> findByUserId(long userId);

    @Query("SELECT CASE WHEN COUNT(tm) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Project p " +
            "JOIN p.teams t " +
            "JOIN t.teamMembers tm " +
            "WHERE p.id = :projectId AND tm.userId = :userId")
    boolean isUserInAnyTeamOfProject(long projectId, long userId);

    @Query("SELECT COUNT(DISTINCT tm.userId) " +
            "FROM Project p " +
            "JOIN p.teams t " +
            "JOIN t.teamMembers tm " +
            "WHERE p.id = :projectId AND tm.userId IN :userIds")
    long countUsersInProjectTeams(long projectId, List<Long> userIds);

    @Query(
            "SELECT tm FROM TeamMember tm JOIN FETCH tm.team t " +
                    "WHERE t.project.id = :projectId"
    )
    List<TeamMember> findAllByProjectId(long projectId);

    @Query(
            "SELECT t FROM TeamMember t JOIN FETCH t.roles tm WHERE tm = :role AND t.team.project.id= :projectId"
    )
    List<TeamMember> findAllByProjectIdWithRoles(@Param("projectId") Long projectId, @Param("role") TeamRole role);
}
