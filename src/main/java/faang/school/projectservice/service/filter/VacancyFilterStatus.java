package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.VacancyDtoFilter;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Stream;

@Component
public class VacancyFilterStatus implements Filter {
    @Override
    public boolean isAplicable(VacancyDtoFilter vacancyDtoFilter) {
        return vacancyDtoFilter.getStatus()!= null;

    }

    @Override
    public Stream<Vacancy> apply(List<Vacancy> vacancy, VacancyDtoFilter filter) {
        return vacancy.stream()
                .filter(vacancy1 -> vacancy1.getStatus().equals(filter.getStatus()));
    }
}
