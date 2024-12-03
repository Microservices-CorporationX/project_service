package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacation.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class MapperVacation {
    @Autowired
    protected ProjectRepository projectRepository;
    @Autowired
    protected CandidateRepository candidateRepository;

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(target = "candidates", expression = "java(createCandidatesDto(vacancy.getCandidates()))")
    public abstract VacancyDto vacancyToVacancyDTo(Vacancy vacancy);

    @Mapping(target = "candidates", expression = "java(createCandidatesVacancy(vacancyDto.id()))")
    @Mapping(target = "project", expression = "java(createProjectVacancy(vacancyDto.projectId()))")
    public abstract Vacancy vacancyDToToVacancy(VacancyDto vacancyDto);

    @Mapping(target = "candidates", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "requiredSkillIds", ignore = true)
    @Mapping(target = "project", expression = "java(createProjectVacancy(vacancyDto.projectId()))")
    public abstract void update(VacancyDto vacancyDto, @MappingTarget Vacancy vacancy);

    List<Candidate> createCandidatesVacancy(Long vacancyId) {
        return candidateRepository.findAllCandidateByVacancyId(vacancyId);
    }

    Project createProjectVacancy(Long projectId) {
        return projectRepository.findById(projectId);
    }

    List<Long> createCandidatesDto(List<Candidate> candidates) {
        return candidates == null ? null : candidates.stream().map(Candidate::getId).toList();
    }
}