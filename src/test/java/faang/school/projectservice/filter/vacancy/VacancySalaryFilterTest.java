package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class VacancySalaryFilterTest {

    private VacancySalaryFilter vacancySalaryFilter;
    private FilterVacancyDto filter;

    @BeforeEach
    void setUp() {
        vacancySalaryFilter = new VacancySalaryFilter();
        filter = FilterVacancyDto.builder().salary(100.0).build();
    }

    @Test
    @DisplayName("Filter is applicable")
    void testIsApplicableTrue() {
        assertTrue(vacancySalaryFilter.isApplicable(filter));
    }

    @Test
    @DisplayName("Filter is not applicable")
    void testIsApplicableFalse() {
        filter.setSalary(null);

        assertFalse(vacancySalaryFilter.isApplicable(filter));
    }

    @Test
    @DisplayName("Filters stream: 2 values in, 1 out")
    void testApplyTwoInOneOut() {
        Stream<Vacancy> vacancies = Stream.of(
                Vacancy.builder().salary(50.0).build(),
                Vacancy.builder().salary(300.0).build()
        );

        Stream<Vacancy> result = vacancySalaryFilter.apply(vacancies, filter);
        List<Vacancy> resultList = result.toList();

        assertNotNull(result);
        assertEquals(1, resultList.size());
        assertEquals(300, resultList.get(0).getSalary());
    }

    @Test
    @DisplayName("Filters stream: 2 values in, 0 out")
    void testApplyTwoInZeroOut() {
        Stream<Vacancy> vacancies = Stream.of(
                Vacancy.builder().salary(50.0).build(),
                Vacancy.builder().salary(70.0).build()
        );

        Stream<Vacancy> result = vacancySalaryFilter.apply(vacancies, filter);
        List<Vacancy> resultList = result.toList();

        assertNotNull(result);
        assertEquals(0, resultList.size());
    }
}