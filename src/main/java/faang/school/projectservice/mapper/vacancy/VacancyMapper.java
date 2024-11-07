package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.client.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface VacancyMapper {
    VacancyDto vacancyToVacancyDto(Vacancy vacancy);

    @Mapping(source = "project.id", target = "projectId")
    Vacancy vacancyDtoToVacancy(VacancyDto dto);
}
