package faang.school.projectservice.filter;

import faang.school.projectservice.dto.internShip.InternshipFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InternshipStatusFilterTest {
    private final InternshipRoleFilter filter = new InternshipRoleFilter();

    @Test
    public void testIsApplicable_TeamRoleNotNull() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder().build();
        filterDto.setTeamRole(TeamRole.INTERN);

        boolean result = filter.isApplicable(filterDto);

        assertTrue(result, "Filter should be applicable when TeamRole is not null.");
    }

    @Test
    public void testIsApplicable_TeamRoleNull() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder().build();
        filterDto.setTeamRole(null);

        boolean result = filter.isApplicable(filterDto);

        assertFalse(result, "Filter should not be applicable when TeamRole is null.");
    }

    @Test
    public void testApply_TeamRoleNotNull() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder().build();
        filterDto.setTeamRole(TeamRole.INTERN);

        TeamMember teamMember1 = new TeamMember();
        teamMember1.setRoles(List.of(TeamRole.INTERN));

        TeamMember teamMember2 = new TeamMember();
        teamMember2.setRoles(List.of(TeamRole.DEVELOPER));

        List<TeamMember> teamMembers = List.of(teamMember1, teamMember2);

        Stream<TeamMember> result = filter.apply(teamMembers.stream(), filterDto);

        assertEquals(List.of(teamMember1), result.collect(Collectors.toList()), "Only members with the role 'INTERN' should be included.");
    }

    @Test
    public void testApply_TeamRoleNoMatch() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder().build();
        filterDto.setTeamRole(TeamRole.INTERN);

        TeamMember teamMember1 = new TeamMember();
        teamMember1.setRoles(List.of(TeamRole.DEVELOPER));

        TeamMember teamMember2 = new TeamMember();
        teamMember2.setRoles(List.of(TeamRole.MANAGER));

        List<TeamMember> teamMembers = List.of(teamMember1, teamMember2);

        Stream<TeamMember> result = filter.apply(teamMembers.stream(), filterDto);

        assertTrue(result.collect(Collectors.toList()).isEmpty(), "No team members should match the 'INTERN' role.");
    }

    @Test
    public void testApply_EmptyTeamMembersList() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder().build();
        filterDto.setTeamRole(TeamRole.INTERN);

        List<TeamMember> teamMembers = List.of();

        Stream<TeamMember> result = filter.apply(teamMembers.stream(), filterDto);

        assertTrue(result.collect(Collectors.toList()).isEmpty(), "The result should be empty if the input list is empty.");
    }
}
