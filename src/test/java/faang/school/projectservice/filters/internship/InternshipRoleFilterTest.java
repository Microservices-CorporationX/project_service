package faang.school.projectservice.filters.internship;

import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InternshipRoleFilterTest {
    private final InternshipRoleFilter internshipRoleFilter = new InternshipRoleFilter();
    private List<Internship> internships;

    @BeforeEach
    public void initFilter() {
        TeamMember firstIntern = TeamMember.builder()
                .roles(List.of(TeamRole.ANALYST, TeamRole.INTERN))
                .build();
        TeamMember secondIntern = TeamMember.builder()
                .roles(List.of(TeamRole.MANAGER, TeamRole.OWNER))
                .build();
        TeamMember thirdIntern = TeamMember.builder()
                .roles(List.of(TeamRole.ANALYST, TeamRole.INTERN))
                .build();
        TeamMember fourthIntern = TeamMember.builder()
                .roles(List.of(TeamRole.DESIGNER, TeamRole.TESTER))
                .build();
        internships = List.of(
                Internship.builder()
                        .interns(List.of(firstIntern, secondIntern, fourthIntern))
                        .build(),
                Internship.builder()
                        .interns(List.of(firstIntern, secondIntern, thirdIntern))
                        .build(),
                Internship.builder()
                        .interns(List.of(fourthIntern, secondIntern))
                        .build()
        );
    }

    @Test
    public void testReturnFalseIfFilterIsNotApplicable() {
        InternshipFilterDto filters = new InternshipFilterDto();

        boolean isApplicable = internshipRoleFilter.isApplicable(filters);

        assertFalse(isApplicable);
    }

    @Test
    public void testReturnTrueIfFilterIsApplicable() {
        InternshipFilterDto filters = InternshipFilterDto.builder()
                .rolePattern(TeamRole.ANALYST)
                .build();

        boolean isApplicable = internshipRoleFilter.isApplicable(filters);

        assertTrue(isApplicable);
    }

    @Test
    public void testReturnFilteredInternshipList() {
        TeamMember firstIntern = TeamMember.builder()
                .roles(List.of(TeamRole.ANALYST, TeamRole.INTERN))
                .build();
        TeamMember thirdIntern = TeamMember.builder()
                .roles(List.of(TeamRole.ANALYST, TeamRole.INTERN))
                .build();
        InternshipFilterDto filters = InternshipFilterDto.builder()
                .rolePattern(TeamRole.ANALYST)
                .build();
        List<Internship> expectedInternships = List.of(
                Internship.builder()
                        .interns(List.of(firstIntern))
                        .build(),
                Internship.builder()
                        .interns(List.of(firstIntern, thirdIntern))
                        .build()
        );

        Stream<Internship> actualUsers = internshipRoleFilter.apply(internships.stream(), filters);

        assertEquals(expectedInternships, actualUsers.toList());
    }
}
