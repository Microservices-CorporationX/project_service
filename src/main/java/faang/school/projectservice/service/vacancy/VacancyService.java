package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.vacancy.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Log
@Service
@Data
@RequiredArgsConstructor
public class VacancyService {
    private VacancyMapper vacancyMapper;
    private VacancyRepository vacancyRepository;
    private ProjectRepository projectRepository;
    private CandidateRepository candidateRepository;
    private TeamMemberJpaRepository teamMemberJpaRepository;
    private List<VacancyFilter> vacancyFilters;
    private UserContext userContext;

    private static final TeamRole CURATOR_ROLE = TeamRole.OWNER;

    public List<VacancyDto> getVacancyIdsByFilters(VacancyFilterDto filterDto) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        log.info("Vacancies loaded");
        return vacancyFilters.stream()
                .filter(vacancyFilter -> vacancyFilter.isApplicable(filterDto))
                .flatMap(vacancyFilterActual -> vacancyFilterActual.apply(vacancies, filterDto))
                .map(vacancy -> vacancyMapper.vacancyToVacancyDto(vacancy))
                .distinct()
                .toList();
    }

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        Long userId = userContext.getUserId();
        Vacancy vacancy = vacancyMapper.vacancyDtoToVacancy(vacancyDto);
        if (!verificationOfCurator(userId)) {
            throw new DataValidationException("Такой куратор не найден");
        }
        vacancy.setProject(projectRepository.getProjectById(vacancyDto.getProjectId()));
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setCreatedBy(userId);
        vacancy.setCandidates(getCandidatesByIds(vacancyDto.getCandidateIds()));
        Vacancy actualVacancy = vacancyRepository.save(vacancy);
        VacancyDto dto = vacancyMapper.vacancyToVacancyDto(actualVacancy);
        dto.setCandidateIds(getCandidateIds(actualVacancy));
        return dto;
    }

    public VacancyDto updateVacancy(Long id, VacancyDto vacancyDto) {
        Vacancy oldVacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Такой задачи не существует"));
        Vacancy actualVacancy = vacancyMapper.vacancyDtoToVacancy(vacancyDto);
        actualVacancy.setId(id);
        actualVacancy.setProject(oldVacancy.getProject());
        actualVacancy.setCandidates(getCandidatesByIds(vacancyDto.getCandidateIds()));
        actualVacancy.setCreatedBy(oldVacancy.getCreatedBy());
        actualVacancy.setUpdatedBy(userContext.getUserId());
        actualVacancy = vacancyRepository.save(actualVacancy);
        VacancyDto resultDto = vacancyMapper.vacancyToVacancyDto(actualVacancy);
        resultDto.setCandidateIds(getCandidateIds(actualVacancy));
        return resultDto;
    }

    public VacancyDto closeVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.findById(vacancyDto.getId())
                .orElseThrow(() -> new DataValidationException("Такой вакансии не существует"));
        if (!validationCompletedVacancy(vacancy)) {
            vacancy.setStatus(VacancyStatus.POSTPONED);
            vacancy = vacancyRepository.save(vacancy);
            log.info("Неудачная попытка закрыть вакансию");
            return vacancyMapper.vacancyToVacancyDto(vacancy);
        }
        deleteVacancy(vacancy);
        vacancy.setStatus(VacancyStatus.CLOSED);
        return vacancyMapper.vacancyToVacancyDto(vacancy);
    }

    private void deleteVacancy(Vacancy vacancy) {
        List<Long> candidateIds = vacancy.getCandidates().stream()
                .filter(candidate -> candidate.getVacancy().getId().equals(vacancy.getId()))
                .filter(candidate -> !candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .map(Candidate::getId)
                .toList();
        candidateRepository.deleteAllById(candidateIds);
        vacancyRepository.deleteById(vacancy.getId());
        log.info("Удачная попытка закрыть вакансию");
    }


    private boolean verificationOfCurator(Long userId) {
        List<TeamMember> teamMembers = teamMemberJpaRepository.findByUserId(userId);
        List<TeamRole> roles = teamMembers.stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .distinct()
                .toList();
        List<Long> listOfProjectOwners = projectRepository.findAll().stream()
                .map(Project::getOwnerId).toList();
        boolean isTheUserReal = listOfProjectOwners.contains(userId);
        boolean rolesContainCurator = roles.contains(CURATOR_ROLE);
        return rolesContainCurator && isTheUserReal;
    }

    private boolean validationCompletedVacancy(Vacancy vacancy) {
        int count = vacancy.getCount();
        List<Candidate> listCandidates = vacancy.getCandidates().stream()
                .filter(candidate -> candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .toList();
        return listCandidates.size() >= count;
    }

    private List<Long> getCandidateIds(Vacancy vacancy) {
        return vacancy.getCandidates().stream()
                .map(Candidate::getId).toList();
    }

    private List<Candidate> getCandidatesByIds(List<Long> candidateIds) {
        return candidateRepository.findAllById(candidateIds);
    }
}
