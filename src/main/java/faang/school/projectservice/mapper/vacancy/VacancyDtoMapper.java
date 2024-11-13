package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.client.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface VacancyDtoMapper {

    Vacancy toEntity(VacancyDto vacancyDto);

    @Mapping(source = "createdBy", target = "supervisor.id")
    @Mapping(source = "count", target = "numberOfCandidates")
    VacancyDto toDto (Vacancy vacancy);
}
