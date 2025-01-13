package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VacancyMapper {
    Vacancy toEntity(VacancyDto dto);

    VacancyDto toDto(Vacancy entity);
}
