package faang.school.projectservice.service.filters.managingFilter;

import faang.school.projectservice.dto.managingTeamDto.TeamMemberFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;

import java.util.stream.Stream;

public class ManagingRoleFilter implements ManagingTeamFilter {
    @Override
    public boolean isApplicable(TeamMemberFilterDto filters) {
        return filters.getRole() != null;
    }

    @Override
    public Stream<TeamMember> apply(Stream<TeamMember> teamMembers, TeamMemberFilterDto filters) {
        TeamRole role = filters.getRole();
        return teamMembers.filter(teamMember -> teamMember.getRoles().contains(role));
    }
}