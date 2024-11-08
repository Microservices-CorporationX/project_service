package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.NewVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(target= "project", ignore = true)
    Vacancy toEntity(NewVacancyDto dto);

    @Mapping(source = "project.id", target = "projectId")
    VacancyDto toDto(Vacancy vacancy);
}
