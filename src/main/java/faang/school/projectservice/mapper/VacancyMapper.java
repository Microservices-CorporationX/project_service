package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.VacancyDto;
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

    @Mapping(target = "project.id", ignore = true)
    @Mapping(target = "candidates", ignore = true)
    Vacancy toEntity(VacancyDto vacancyDto);

    List<VacancyDto> toDto(List<Vacancy> vacancies);
    List<Vacancy> toEntity(List<VacancyDto> vacancyDtos);

    @Named("mapCandidatesIds")
    default List<Long> mapCandidatesIds(List<Candidate> candidates) {
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }

}
