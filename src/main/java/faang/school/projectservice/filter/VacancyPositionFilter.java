package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacation.FilterVacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;
@Component
public class VacancyPositionFilter implements VacancyFilter{
    @Override
    public boolean isAvailable(FilterVacancyDto filterVacancyDto) {
        return filterVacancyDto.salary() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, FilterVacancyDto filterVacancyDto) {
        return vacancyStream.filter(vacancy -> vacancy.getSalary() >= filterVacancyDto.salary());
    }
}
