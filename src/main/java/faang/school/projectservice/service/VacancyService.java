package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectService projectService;
    private final CandidateService candidateService;
    private final VacancyValidator vacancyValidator;
    private final ProjectValidator projectValidator;
    private final List<Filter<Vacancy, FilterVacancyDto>> vacancyFilters;

    @Transactional
    public VacancyDto create(VacancyDto vacancyDto) {
        projectValidator.validateProjectExistsById(vacancyDto.getProjectId());
        vacancyValidator.validateVacancyCreatorRole(vacancyDto);
        Vacancy vacancy = toEntityFromDto(vacancyDto);
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(vacancy);
    }

    @Transactional
    public VacancyDto updateVacancyStatus(VacancyDto dto) {
        vacancyValidator.validateVacancyCreatorRole(dto);
        Vacancy vacancy = getVacancyById(dto.getId());
        if (dto.getStatus().equals(VacancyStatus.CLOSED)) {
            vacancyValidator.validateCandidateCountForClosure(vacancy);
            vacancy.setStatus(dto.getStatus());
            vacancyRepository.save(vacancy);
        }
        return vacancyMapper.toDto(vacancy);
    }

    @Transactional
    public void deleteVacancy(long vacancyId) {
        vacancyValidator.validateVacancyExistsById(vacancyId);
        getRejectedCandidatesIds(vacancyId).forEach(candidateService::deleteCandidateById);
        vacancyRepository.deleteById(vacancyId);
    }

    public List<VacancyDto> filterVacancies(FilterVacancyDto filters) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        return vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(vacancies, filters))
                .map(vacancyMapper::toDto)
                .toList();
    }

    public List<Candidate> getCandidatesByVacancyId(Long vacancyId) {
        return getVacancyById(vacancyId).getCandidates();
    }

    public Vacancy getVacancyById(long vacancyId) {
        return vacancyRepository.findById(vacancyId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Vacancy not found by id: %s", vacancyId)));
    }

    private Vacancy toEntityFromDto(VacancyDto dto) {
        Vacancy vacancy = vacancyMapper.toEntity(dto);
        vacancy.setProject(projectService.getProjectById(dto.getProjectId()));
        return vacancy;
    }

    private List<Long> getRejectedCandidatesIds(Long vacancyId) {
        return getCandidatesByVacancyId(vacancyId).stream()
                .filter(candidate -> candidate.getVacancy().getId().equals(vacancyId))
                .map(Candidate::getId)
                .toList();
    }
}
