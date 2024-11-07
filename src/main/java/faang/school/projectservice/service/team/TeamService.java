package faang.school.projectservice.service.team;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;

import java.util.Optional;

public interface TeamService {
    void deleteMemberByUserId(Long userId);
    Optional<Team> findTeamByProjectId(Long projectId);
    Optional<TeamMember> findMemberByUserIdAndProjectId(Long teamMemberId, Long projectId);
}
