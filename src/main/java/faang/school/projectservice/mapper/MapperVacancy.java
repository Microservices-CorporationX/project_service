package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MapperVacancy {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(target = "candidates", expression = "java(createCandidatesDto(vacancy))")
    VacancyDto vacancyToVacancyDTo(Vacancy vacancy);

    @Mapping(target = "candidates", ignore = true)
    @Mapping(target = "requiredSkillIds", ignore = true)
    @Mapping(source = "projectId", target = "project.id")
    Vacancy vacancyDToToVacancy(VacancyDto vacancyDto);

    @Mapping(target = "candidates", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "requiredSkillIds", ignore = true)
    @Mapping(target = "project", expression = "java(createProjectForUpdate(vacancyDto, vacancy))")
    void update(VacancyDto vacancyDto, @MappingTarget Vacancy vacancy);

    default List<Long> createCandidatesDto(Vacancy vacancy) {
        return (vacancy == null || vacancy.getCandidates() == null)
                ? null
                : vacancy.getCandidates().stream().map(Candidate::getId).toList();
    }

    default Project createProjectForUpdate(VacancyDto vacancyDto, Vacancy vacancy) {
        if (vacancy == null) {
            return null;
        }
        if (vacancyDto == null
                || vacancyDto.projectId() == null
                || vacancyDto.projectId().equals(vacancy.getProject().getId())) {
            return vacancy.getProject();
        }
        return Project.builder().id(vacancyDto.id()).build();
    }
}