package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class FilterVacancySalary implements FilterVacancy {
    @Override
    public boolean isAvailable(FilterVacancyDto filterVacancyDto) {
        return filterVacancyDto.salary() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, FilterVacancyDto filterVacancyDto) {
        return vacancyStream.filter(vacancy -> vacancy.getSalary() >= filterVacancyDto.salary());
    }
}