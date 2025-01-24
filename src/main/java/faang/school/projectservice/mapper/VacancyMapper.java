package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "candidates", source = "candidatesId", qualifiedByName = "mapCandidatesIdsToCandidates")
    Vacancy toEntity(VacancyDto vacancyDto);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "candidatesId", source = "candidates", qualifiedByName = "mapCandidatesToCandidatesIds")
    VacancyDto toDto(Vacancy vacancy);

    List<VacancyDto> toDtoList(List<Vacancy> vacancies);

    @Named("mapCandidatesIdsToCandidates")
    default List<Candidate> mapCandidatesIdsToCandidates(List<Long> candidatesIds) {
        if (candidatesIds == null) {
            return List.of();
        }

        return candidatesIds.stream()
                .map(id -> {
                    Candidate candidate = new Candidate();
                    candidate.setId(id);
                    return candidate;
                }).toList();
    }

    @Named("mapCandidatesToCandidatesIds")
    default List<Long> mapCandidatesToCandidatesIds(List<Candidate> candidates) {
        if (candidates == null) {
            return List.of();
        }
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }
}
