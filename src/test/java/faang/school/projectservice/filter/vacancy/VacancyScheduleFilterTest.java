package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.WorkSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class VacancyScheduleFilterTest {

    private VacancyScheduleFilter vacancyScheduleFilter;
    private FilterVacancyDto filter;

    @BeforeEach
    void setUp() {
        vacancyScheduleFilter = new VacancyScheduleFilter();
        filter = FilterVacancyDto.builder().workSchedule(WorkSchedule.FULL_TIME).build();
    }

    @Test
    @DisplayName("Filter is applicable")
    void testIsApplicableTrue() {
        assertTrue(vacancyScheduleFilter.isApplicable(filter));
    }

    @Test
    @DisplayName("Filter is not applicable")
    void testIsApplicableFalse() {
        filter.setWorkSchedule(null);

        assertFalse(vacancyScheduleFilter.isApplicable(filter));
    }

    @Test
    @DisplayName("Filters stream: 2 values in, 1 out")
    void testApplyTwoInOneOut() {
        Stream<Vacancy> vacancies = Stream.of(
                Vacancy.builder().workSchedule(WorkSchedule.FULL_TIME).build(),
                Vacancy.builder().workSchedule(WorkSchedule.PART_TIME).build()
        );

        Stream<Vacancy> result = vacancyScheduleFilter.apply(vacancies, filter);
        List<Vacancy> resultList = result.toList();

        assertNotNull(result);
        assertEquals(1, resultList.size());
        assertEquals(WorkSchedule.FULL_TIME, resultList.get(0).getWorkSchedule());
    }

    @Test
    @DisplayName("Filters stream: 2 values in, 0 out")
    void testApplyTwoInZeroOut() {
        Stream<Vacancy> vacancies = Stream.of(
                Vacancy.builder().workSchedule(WorkSchedule.PART_TIME).build(),
                Vacancy.builder().workSchedule(WorkSchedule.SHIFT_WORK).build()
        );

        Stream<Vacancy> result = vacancyScheduleFilter.apply(vacancies, filter);
        List<Vacancy> resultList = result.toList();

        assertNotNull(result);
        assertEquals(0, resultList.size());
    }
}