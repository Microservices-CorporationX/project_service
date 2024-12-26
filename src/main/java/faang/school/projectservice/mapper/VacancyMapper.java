package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VacancyMapper {

    @BeanMapping(ignoreByDefault = true)
    Vacancy toEntity(VacancyCreateDto dto);

    VacancyDto toDto(Vacancy dto);

    @BeanMapping(ignoreByDefault = true)
    Vacancy toEntity(VacancyUpdateDto dto);

    List<VacancyDto> toDto(List<Vacancy> vacancies);
}
