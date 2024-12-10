package faang.school.projectservice.service.filters.managingFilter;
import faang.school.projectservice.dto.managingTeamDto.TeamMemberFilterDto;
import faang.school.projectservice.model.TeamMember;

import java.util.stream.Stream;

public class ManagingFilter implements ManagingTeamFilter {
    @Override
    public boolean isApplicable(TeamMemberFilterDto filters) {
        return filters.getName() != null && !filters.getName().isEmpty();
    }

    @Override
    public Stream<TeamMember> apply(Stream<TeamMember> teamMembers, TeamMemberFilterDto filters) {
        return teamMembers.filter(teamMember -> teamMember.getName().contains(filters.getName()));
    }
}
