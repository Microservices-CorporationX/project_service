package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(source = "candidates", target = "candidatesIds", qualifiedByName = "mapCandidatesIds")
    @Mapping(source = "project.id", target = "projectId")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "candidatesIds", target = "candidates", qualifiedByName = "mapCandidates")
    Vacancy toEntity(VacancyDto vacancyDto);

    @Named("mapCandidatesIds")
    default List<Long> mapCandidatesIds(List<Candidate> candidates) {
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }

    @Named("mapCandidates")
    default List<Candidate> mapCandidates(List<Long> candidatesIds) {
        return candidatesIds.stream()
                .map(id -> {
                    Candidate candidate = new Candidate();
                    candidate.setId(id);
                    return candidate;
                })
                .toList();
    }
}
