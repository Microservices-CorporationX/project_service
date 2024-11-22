package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.VacancyDtoFilter;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Stream;

public interface Filter {
    boolean isAplicable(VacancyDtoFilter vacancyDtoFilter);

    Stream<Vacancy> apply(List<Vacancy> vacancy, VacancyDtoFilter filter);
}