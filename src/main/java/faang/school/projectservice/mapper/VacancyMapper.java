package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(source = "idProject", target = "project.id")
    Vacancy toEntity(VacancyDto vacancyDto);
    @Mapping(source = "project.id", target ="idProject")
    VacancyDto toDto(Vacancy vacancy);
}