package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.CreateVacancyRequest;
import faang.school.projectservice.dto.vacancy.CreateVacancyResponse;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    @Mapping(target="id", ignore = true)
    Vacancy fromCreateRequest(CreateVacancyRequest createRequest);

    @Mapping(target="projectId", ignore = true)
    @Mapping(source="project.id", target="projectId")
    CreateVacancyResponse toCreateResponse(Vacancy vacancy);
}
