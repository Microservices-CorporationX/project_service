package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.mapper.vacancy.VacancyDtoMapper;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.candidate.CandidateService;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class VacancyService {
    private static final String PROJECT_NOT_FOUND = "Project not found";
    private static final String TEAM_MEMBER_NOT_FOUND = "Team member not found";
    private static final String VACANCY_NOT_FOUND = "Vacancy not found";

    private final VacancyRepository vacancyRepository;
    private final ProjectService projectService;
    private final CandidateService candidateService;
    private final TeamMemberService teamMemberService;
    private final VacancyDtoMapper vacancyDtoMapper;
    private final VacancyValidator vacancyValidator;
    private final List<VacancyFilter> vacancyFilters;

    public VacancyDto createVacancy(VacancyDto vacancyDto){
        Project project = projectService.findProject(vacancyDto.projectId()).orElseThrow(
                () -> new EntityNotFoundException(PROJECT_NOT_FOUND));
        TeamMember supervisor = teamMemberService.findTeamMember(vacancyDto.supervisor().id()).orElseThrow(
                () -> new EntityNotFoundException(TEAM_MEMBER_NOT_FOUND));

        vacancyValidator.validateSupervisorHasOwnerRole(supervisor);
        vacancyValidator.validateUniqueVacancy(project, vacancyDto);

        Vacancy vacancy = vacancyDtoMapper.toEntity(vacancyDto);
        vacancy.setProject(project);
        vacancy.setCreatedBy(supervisor.getUserId());
        vacancy.setCount(vacancyDto.numberOfCandidates());
        vacancy.setStatus(VacancyStatus.OPEN);

        Vacancy createdVacancy = vacancyRepository.save(vacancy);
        log.info("Vacancy with id: {} was created ", vacancyDto.id());

        return vacancyDtoMapper.toDto(createdVacancy);
    }

    public VacancyDto getVacancy(Long id){
        Vacancy vacancy = vacancyRepository.findById(id).orElseThrow(
                () -> (new EntityNotFoundException(VACANCY_NOT_FOUND)));

        return vacancyDtoMapper.toDto(vacancy);
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto){
        if (!vacancyRepository.existsById(vacancyDto.id())){
            log.error("Vacancy with id: {} does not exist", vacancyDto.id());
            throw new EntityNotFoundException("Vacancy with does not exists");
        }
        int requiredCandidates = vacancyDto.numberOfCandidates();
        int numberOfCandidates = candidateService.getAllCandidatesByVacancy(vacancyDto.id()).size();
        vacancyValidator.validateCanCloseVacancy(vacancyDto, requiredCandidates, numberOfCandidates);

        Vacancy vacancy = vacancyRepository.findById(vacancyDto.id()).orElseThrow(
                () -> new EntityNotFoundException(VACANCY_NOT_FOUND));
        Project project = projectService.findProject(vacancyDto.projectId()).orElseThrow(
                () -> new EntityNotFoundException(PROJECT_NOT_FOUND));
        TeamMember supervisor = teamMemberService.findTeamMember(vacancyDto.supervisor().id()).orElseThrow(
                () -> new EntityNotFoundException(TEAM_MEMBER_NOT_FOUND));

        vacancy.setProject(project);
        vacancy.setCreatedBy(supervisor.getId());
        vacancy.setName(vacancyDto.name());
        vacancy.setDescription(vacancyDto.description());
        vacancy.setStatus(vacancyDto.status());
        vacancy.setCount(requiredCandidates);
        vacancy.setUpdatedAt(LocalDateTime.now());

        Vacancy updatedVacancy = vacancyRepository.save(vacancy);
        log.info("Vacancy with id: {} was updated ", vacancyDto.id());

        return vacancyDtoMapper.toDto(updatedVacancy);
    }

    @Transactional
    public void deleteVacancy(Long vacancyId){
        if (!vacancyRepository.existsById(vacancyId)) {
            log.error("Vacancy with id: {} was not found", vacancyId);
            throw new EntityNotFoundException(VACANCY_NOT_FOUND);
        }

        List<Long> candidateUserIdsToDelete = candidateService.getAllCandidatesByVacancy(vacancyId);

        if (!candidateUserIdsToDelete.isEmpty()) {
            candidateService.deleteCandidates(candidateUserIdsToDelete);
            log.info("Rejected or queued candidates were deleted");
        }

        vacancyRepository.deleteById(vacancyId);
        log.info("Vacancy with id: {} was deleted", vacancyId);
    }

    public List<VacancyDto> getFilteredVacancies(Long projectId, VacancyFilterDto filters){
        Project project = projectService.findProject(projectId).orElseThrow(
                () -> new EntityNotFoundException(PROJECT_NOT_FOUND));
        Stream<Vacancy> vacancies = project.getVacancies().stream();

        return vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(vacancies, filters))
                .map(vacancyDtoMapper::toDto)
                .toList();
    }
}
