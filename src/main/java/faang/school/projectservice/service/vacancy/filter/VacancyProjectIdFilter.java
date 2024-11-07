package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.client.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public class VacancyProjectIdFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getProjectId() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancyStream, VacancyFilterDto filters) {
        return vacancyStream.filter(vacancy -> vacancy.getId().equals(filters.getProjectId()));
    }
}
