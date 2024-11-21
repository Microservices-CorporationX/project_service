package faang.school.projectservice.filter.members;

import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.model.TeamMember;

import java.util.stream.Stream;

public interface TeamMemberFilter {
    boolean isApplicable(TeamFilterDto teamFilterDto);

    Stream<TeamMember> apply(Stream<TeamMember> teamMembers, TeamFilterDto teamFilterDto);
}
