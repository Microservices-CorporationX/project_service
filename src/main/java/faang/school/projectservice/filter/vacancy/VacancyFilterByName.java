package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.filter.Filter;

import java.util.stream.Stream;

public class VacancyFilterByName implements Filter<VacancyFilterDto, Vacancy> {

    @Override
    public boolean isApplicable(VacancyFilterDto filterDto) {
        return filterDto.getNamePattern().isBlank();
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> itemStream, VacancyFilterDto filterDto) {
        return itemStream.filter(vacancy -> vacancy.getName().equalsIgnoreCase(filterDto.getNamePattern()));
    }
}
