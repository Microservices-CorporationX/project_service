package faang.school.projectservice.filter.teammember;

import faang.school.projectservice.dto.teammember.TeamMemberFilterDto;
import faang.school.projectservice.model.TeamMember;

import java.util.stream.Stream;

public interface TeamMemberFilter {
        boolean isApplicable(TeamMemberFilterDto stageDto);

        Stream<TeamMember> apply(Stream<TeamMember> stages, TeamMemberFilterDto stageFilterDto);
}
