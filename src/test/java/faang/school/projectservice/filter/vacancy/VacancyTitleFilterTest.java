package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class VacancyTitleFilterTest {

    private VacancyTitleFilter vacancyTitleFilter;
    private FilterVacancyDto filter;

    @BeforeEach
    void setUp() {
        vacancyTitleFilter = new VacancyTitleFilter();
        filter = FilterVacancyDto.builder().title("Foo").build();
    }

    @Test
    @DisplayName("Filter is applicable")
    void testIsApplicableTrue() {
        assertTrue(vacancyTitleFilter.isApplicable(filter));
    }

    @Test
    @DisplayName("Filter is not applicable")
    void testIsApplicableFalse() {
        filter.setTitle(null);

        assertFalse(vacancyTitleFilter.isApplicable(filter));
    }

    @Test
    @DisplayName("Filters stream: 2 values in, 1 out")
    void testApplyTwoInOneOut() {
        Stream<Vacancy> vacancies = Stream.of(
                Vacancy.builder().name("Foo").build(),
                Vacancy.builder().name("Bar").build()
        );

        Stream<Vacancy> result = vacancyTitleFilter.apply(vacancies, filter);
        List<Vacancy> resultList = result.toList();

        assertNotNull(result);
        assertEquals(1, resultList.size());
        assertEquals("Foo", resultList.get(0).getName());
    }

    @Test
    @DisplayName("Filters stream: 2 values in, 0 out")
    void testApplyTwoInZeroOut() {
        Stream<Vacancy> vacancies = Stream.of(
                Vacancy.builder().name("Java").build(),
                Vacancy.builder().name("Bar").build()
        );

        Stream<Vacancy> result = vacancyTitleFilter.apply(vacancies, filter);
        List<Vacancy> resultList = result.toList();

        assertNotNull(result);
        assertEquals(0, resultList.size());
    }
}