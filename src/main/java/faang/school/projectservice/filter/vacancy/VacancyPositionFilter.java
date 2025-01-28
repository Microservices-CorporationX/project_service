package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public class VacancyPositionFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getPositionPattern() != null;
    }

    @Override
    public void apply(Stream<Vacancy> vacancies, VacancyFilterDto filters) {
        vacancies.filter(vacancy -> vacancy.getPosition() == filters.getPositionPattern());
    }
}
