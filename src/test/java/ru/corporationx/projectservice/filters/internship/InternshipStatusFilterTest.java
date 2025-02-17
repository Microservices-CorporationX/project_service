package ru.corporationx.projectservice.filters.internship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.corporationx.projectservice.filters.internship.InternshipStatusFilter;
import ru.corporationx.projectservice.model.dto.internship.InternshipFilterDto;
import ru.corporationx.projectservice.model.entity.Internship;
import ru.corporationx.projectservice.model.entity.InternshipStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InternshipStatusFilterTest {
    private final InternshipStatusFilter internshipStatusFilter = new InternshipStatusFilter();
    private List<Internship> internships;

    @BeforeEach
    public void initFilter() {
        internships = List.of(
                Internship.builder()
                        .status(InternshipStatus.IN_PROGRESS)
                        .build(),
                Internship.builder()
                        .status(InternshipStatus.COMPLETED)
                        .build(),
                Internship.builder()
                        .status(InternshipStatus.IN_PROGRESS)
                        .build(),
                Internship.builder()
                        .status(InternshipStatus.IN_PROGRESS)
                        .build()
        );
    }

    @Test
    public void testReturnFalseIfFilterIsNotApplicable() {
        InternshipFilterDto filters = new InternshipFilterDto();

        boolean isApplicable = internshipStatusFilter.isApplicable(filters);

        assertFalse(isApplicable);
    }

    @Test
    public void testReturnTrueIfFilterIsApplicable() {
        InternshipFilterDto filters = InternshipFilterDto.builder()
                .statusPattern(InternshipStatus.COMPLETED)
                .build();

        boolean isApplicable = internshipStatusFilter.isApplicable(filters);

        assertTrue(isApplicable);
    }

    @Test
    public void testReturnFilteredInternshipList() {
        InternshipFilterDto filters = InternshipFilterDto.builder()
                .statusPattern(InternshipStatus.COMPLETED)
                .build();
        List<Internship> expectedInternships = List.of(
                Internship.builder()
                        .status(InternshipStatus.COMPLETED)
                        .build()
        );

        Stream<Internship> actualUsers = internshipStatusFilter.apply(internships.stream(), filters);

        assertEquals(expectedInternships, actualUsers.toList());
    }
}
