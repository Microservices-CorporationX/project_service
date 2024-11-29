package faang.school.projectservice.mapper.vacancy;

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

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "candidates", target = "candidateIds", qualifiedByName = "candidatesToIds")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "candidates", ignore = true)
    Vacancy toEntity(VacancyDto vacancyDto);

    List<VacancyDto> toDto(List<Vacancy> vacancies);

    List<Vacancy> toEntity(List<VacancyDto> vacancyDtos);

    @Named("candidatesToIds")
    default List<Long> map(List<Candidate> candidates){
        return candidates.stream().map(Candidate::getId).toList();
    }
}
