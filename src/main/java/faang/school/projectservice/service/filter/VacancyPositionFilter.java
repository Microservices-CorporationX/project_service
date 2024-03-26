package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class VacancyPositionFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto vacancyFilterDto) {
        return vacancyFilterDto.getStatus() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto vacancyFilterDto) {
        return vacancies.filter(vacancy -> vacancy.getStatus().equals(vacancyFilterDto.getStatus()));
    }
}
