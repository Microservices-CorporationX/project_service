package faang.school.projectservice.filter;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class InternshipRoleFilterTest {
    private final InternshipRoleFilter filter = new InternshipRoleFilter();

    @Test
    public void testIsApplicable_TeamRoleNotNull() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder().build();
        filterDto.setTeamRole(TeamRole.INTERN);

        boolean result = filter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableTeamRoleNull() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder().build();
        filterDto.setTeamRole(null);

        boolean result = filter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    public void testApplyTeamRoleNotNull() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder().build();
        filterDto.setTeamRole(TeamRole.INTERN);

        TeamMember teamMember1 = new TeamMember();
        teamMember1.setRoles(List.of(TeamRole.INTERN));

        TeamMember teamMember2 = new TeamMember();
        teamMember2.setRoles(List.of(TeamRole.DEVELOPER));

        List<TeamMember> teamMembers = List.of(teamMember1, teamMember2);

        Stream<TeamMember> result = filter.apply(teamMembers.stream(), filterDto);

        assertEquals(List.of(teamMember1), result.collect(Collectors.toList()));
    }
}
