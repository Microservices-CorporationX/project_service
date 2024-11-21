package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public class VacancyNameFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getName() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, VacancyFilterDto filters) {
        return vacancyStream.filter(vacancy -> vacancy.getName().toLowerCase().contains(filters.getName().toLowerCase()));
    }
}
