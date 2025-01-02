package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface FilterVacancy {
    boolean isAvailable(FilterVacancyDto filterVacancyDto);

    Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, FilterVacancyDto filterVacancyDto);
}
