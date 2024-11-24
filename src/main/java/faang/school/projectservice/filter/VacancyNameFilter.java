package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacation.FilterVacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;
@Component
public class VacancyNameFilter implements VacancyFilter{
    @Override
    public boolean isAvailable(FilterVacancyDto filterVacancyDto) {
        return !filterVacancyDto.name().isEmpty();
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, FilterVacancyDto filterVacancyDto) {
        return vacancyStream.filter(vacancy -> vacancy.getName().contains(filterVacancyDto.name()));
    }
}