package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.VacancyDtoFilter;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Stream;

@Component
public class VacancyFilterName implements Filter {
    @Override
    public boolean isAplicable(VacancyDtoFilter vacancyDtoFilter) {
        return vacancyDtoFilter.getName() != null;

    }

    @Override
    public Stream<Vacancy> apply(List<Vacancy> vacancies, VacancyDtoFilter filter) {
        return vacancies.stream()
                .filter(vacancy1 -> vacancy1.getName().equals(filter.getName()));
    }
}
