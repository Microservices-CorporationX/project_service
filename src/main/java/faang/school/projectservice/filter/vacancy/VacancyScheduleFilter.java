package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyScheduleFilter implements Filter<Vacancy, FilterVacancyDto> {

    @Override
    public boolean isApplicable(FilterVacancyDto filter) {
        return filter.getWorkSchedule() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, FilterVacancyDto filter) {
        return vacancyStream.filter(vacancy -> vacancy.getWorkSchedule() == filter.getWorkSchedule());
    }
}
