package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
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
