package faang.school.projectservice.service.filters.managingFilter;
import faang.school.projectservice.dto.managingTeamDto.TeamMemberFilterDto;
import faang.school.projectservice.model.TeamMember;

import java.util.stream.Stream;

public interface ManagingTeamFilter {
    boolean isApplicable(TeamMemberFilterDto filters);
    Stream<TeamMember> apply(Stream<TeamMember> teamMembers, TeamMemberFilterDto filters);
}
