package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.CreateVacancyRequest;
import faang.school.projectservice.dto.vacancy.CreateVacancyResponse;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequest;
import faang.school.projectservice.dto.vacancy.UpdateVacancyResponse;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "candidates", ignore = true)
    Vacancy fromCreateRequest(CreateVacancyRequest createRequest);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "candidates", ignore = true)
    Vacancy fromUpdateRequest(UpdateVacancyRequest updateRequest);

    @Mapping(source = "project.id", target = "projectId")
    CreateVacancyResponse toCreateResponse(Vacancy vacancy);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "candidates", target = "candidateIds", qualifiedByName = "mapCandidatesToIds")
    UpdateVacancyResponse toUpdateResponse(Vacancy vacancy);

    @Named("mapCandidatesToIds")
    default List<Long> mapCandidatesToIds(List<Candidate> candidates) {
        return candidates.stream().map(Candidate::getId).toList();
    }
}
