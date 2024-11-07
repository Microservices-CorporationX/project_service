package faang.school.projectservice.service.vacancy;

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
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.filter.Filter;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@Data
@RequiredArgsConstructor
public class VacancyService {
    VacancyMapper vacancyMapper;
    VacancyRepository vacancyRepository;
    ProjectRepository projectRepository;
    TeamRepository teamRepository;
    CandidateRepository candidateRepository;
    TeamMemberRepository teamMemberRepository;
    TeamMemberJpaRepository teamMemberJpaRepository;
    List<Filter<VacancyFilterDto, Vacancy>> filters;

    private static final TeamRole CURATOR_ROLE = TeamRole.OWNER;

    public List<VacancyDto> getVacancyIdsByFilters(VacancyFilterDto filterDto) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filterActual -> filterActual.apply(vacancies, filterDto))
                .map(vacancyMapper::vacancyToVacancyDto)
                .toList();
    }

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyMapper.vacancyDtoToVacancy(vacancyDto);
        TeamMember teamMember = teamMemberJpaRepository.findById(vacancy.getCreatedBy())
                .orElseThrow(() -> new DataValidationException("Team member not found"));
        if (validationVacancy(teamMember)) {
            throw new DataValidationException("Такой куратор не найден");
        }
        vacancy.setStatus(VacancyStatus.OPEN);
        Vacancy actualVacancy = vacancyRepository.save(vacancy);
        VacancyDto dto = vacancyMapper.vacancyToVacancyDto(actualVacancy);
        dto.setCandidateIds(getCandidateIds(actualVacancy));
        return dto;
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyMapper.vacancyDtoToVacancy(vacancyDto);
        List<Candidate> candidates = candidateRepository.findAllById(vacancyDto.getCandidateIds());
        vacancy.setCandidates(candidates);
        vacancy.setStatus(VacancyStatus.POSTPONED);
        Vacancy vacancyOut = vacancyRepository.save(vacancy);
        return vacancyMapper.vacancyToVacancyDto(vacancyOut);
    }

    public void closeVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.findById(vacancyDto.getId())
                .orElseThrow(() -> new DataValidationException("Такой вакансии не существует"));
        if (!validationCompletedVacancy(vacancy)) {
            vacancy.setStatus(VacancyStatus.POSTPONED);
            vacancy = vacancyRepository.save(vacancy);
            vacancyMapper.vacancyToVacancyDto(vacancy);
        }
        deleteVacancy(vacancy);
    }

    private void deleteVacancy(Vacancy vacancy) {
        List<Long> candidateIds = vacancy.getCandidates().stream()
                .filter(candidate -> candidate.getVacancy().getId().equals(vacancy.getId()))
                .filter(candidate -> !candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .map(Candidate::getId)
                .toList();
        candidateRepository.deleteAllById(candidateIds);
        vacancyRepository.deleteById(vacancy.getId());
    }


    private boolean validationVacancy(TeamMember teamMember) {
        Long curatorId = teamMember.getUserId();
        List<TeamRole> roles = teamMember.getRoles();
        List<Long> listOfProjectOwners = projectRepository.findAll().stream()
                .map(Project::getOwnerId).toList();
        boolean isTheUserReal = listOfProjectOwners.contains(curatorId);
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
}
