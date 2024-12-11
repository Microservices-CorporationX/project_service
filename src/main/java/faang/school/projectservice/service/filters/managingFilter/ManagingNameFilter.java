package faang.school.projectservice.service.filters.managingFilter;

import faang.school.projectservice.dto.managingTeamDto.TeamMemberFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;

import java.util.stream.Stream;

public class ManagingNameFilter implements ManagingTeamFilter {

    @Override
    public boolean isApplicable(TeamMemberFilterDto filters) {
        return (filters.getName() != null && !filters.getName().isEmpty()) || filters.getRole() != null ;
    }

    @Override
    public Stream<TeamMember> apply(Stream<TeamMember> teamMembers, TeamMemberFilterDto filters) {

        return teamMembers.filter(teamMember -> teamMember.getName().contains(filters.getName()) || teamMember.getRoles().contains(filters.getRole())
        );
    }
}