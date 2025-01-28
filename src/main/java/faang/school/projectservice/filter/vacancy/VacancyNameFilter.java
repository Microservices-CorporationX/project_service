package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public class VacancyNameFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public void apply(Stream<Vacancy> vacancies, VacancyFilterDto filters) {
        vacancies.filter(vacancy -> vacancy.getName().matches(filters.getNamePattern()));
    }
}
