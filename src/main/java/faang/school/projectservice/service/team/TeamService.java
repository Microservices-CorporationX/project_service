package faang.school.projectservice.service.team;

import faang.school.projectservice.model.TeamMember;

public interface TeamService {
    void deleteMemberByUserId(Long userId);
    TeamMember findMemberByUserIdAndProjectId(Long teamMemberId, Long projectId);
}
