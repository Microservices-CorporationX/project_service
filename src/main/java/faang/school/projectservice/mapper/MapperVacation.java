package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacation.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MapperVacation {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(target = "candidates", expression = "java(createCandidatesDto(vacancy.getCandidates()))")
    VacancyDto vacancyToVacancyDTo(Vacancy vacancy);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(target = "candidates", expression = "java(createCandidatesVacancy())")
    Vacancy vacancyDToToVacancy(VacancyDto vacancyDto);

    @Mapping(target = "candidates", expression = "java(createCandidatesVacancy())")
    void update(VacancyDto vacancyDto, @MappingTarget Vacancy vacancy);

    default List<Candidate> createCandidatesVacancy() {
        return null;
    }

    default List<Long> createCandidatesDto(List<Candidate> candidates) {
        return candidates == null ? null : candidates.stream().map(Candidate::getId).toList();
    }
}