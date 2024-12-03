package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacation.FilterVacancyDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class FilterVacancyNameTest {
    public static final LocalDateTime PRE_SET_LOCAL_DATE_TIME = LocalDateTime.now();
    @Spy
    FilterVacancyName filterVacancy;

    @Test
    void testIsAvailableTrueSuccess() {
        FilterVacancyDto filterVacancyDto = new FilterVacancyDto("Filter Name", 1.00);
        assertTrue(filterVacancy.isAvailable(filterVacancyDto));
    }

    @Test
    void testIsAvailableFalseSuccess() {
        FilterVacancyDto filterVacancyDto = new FilterVacancyDto("", 1.00);
        assertFalse(filterVacancy.isAvailable(filterVacancyDto));
    }

    @Test
    void testApplySuccess() {
        FilterVacancyDto filterVacancyDto = new FilterVacancyDto("1", 1.00);
        List<Vacancy> initVacancies = getVacancies(3);

        assertEquals(1, filterVacancy.apply(initVacancies.stream(), filterVacancyDto).toList().stream().count());

    }

    public List<Vacancy> getVacancies(int number) {
        List<Vacancy> vacancies = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            vacancies.add(getVacancy((long) i));
        }
        return vacancies;
    }

    public Vacancy getVacancy(Long number) {
        return new Vacancy(
                1L + number,
                "Name Vacancy " + number,
                "description",
                null,
                null,
                PRE_SET_LOCAL_DATE_TIME,
                PRE_SET_LOCAL_DATE_TIME,
                1L,
                1L,
                VacancyStatus.OPEN,
                100.00 - number,
                WorkSchedule.FULL_TIME,
                3,
                null);
    }
}