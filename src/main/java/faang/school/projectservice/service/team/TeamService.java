package faang.school.projectservice.service.team;

import faang.school.projectservice.model.TeamMember;

import java.util.Optional;

public interface TeamService {
    void deleteMemberByUserId(Long userId);
    Optional<TeamMember> findTeamMemberByUserIdAndProjectId(Long teamMemberId, Long projectId);
}
