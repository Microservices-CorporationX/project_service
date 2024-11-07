package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.client.vacancy.VacancyDto;
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
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private static final TeamRole CURATOR_ROLE = TeamRole.OWNER;
    private static final TeamRole ROLE_CANDIDATE = TeamRole.DEVELOPER;
    private static final int MAX_JUNIOR_DEV = 5;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyMapper.vacancyDtoToVacancy(vacancyDto);
        TeamMember teamMember = teamMemberJpaRepository.findById(vacancy.getCreatedBy())
                .orElseThrow(() -> new DataValidationException("Team member not found"));
        Long curatorId = teamMember.getUserId();
        List<TeamRole> roles = teamMember.getRoles();
        if (validationVacancy(curatorId, roles)) {
            throw new DataValidationException("Такой куратор не найден");
        }
        vacancy.setStatus(VacancyStatus.OPEN);
        Vacancy actualVacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.vacancyToVacancyDto(actualVacancy);
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyMapper.vacancyDtoToVacancy(vacancyDto);
        List<Candidate> candidates = candidateRepository.findAllById(vacancyDto.getCandidateIds());
        vacancy.setCandidates(candidates);
        Vacancy vacancyOut = vacancyRepository.save(vacancy);
        return vacancyMapper.vacancyToVacancyDto(vacancyOut);
    }

    public void closeVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.findById(vacancyDto.getId())
                .orElseThrow(() -> new DataValidationException("Такой вакансии не существует"));
        if (!validationCompletedVacancy(vacancyDto)) {
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


    private boolean validationVacancy(Long curatorId, List<TeamRole> roles) {
        boolean rolesContainCurator = roles.contains(CURATOR_ROLE);
        List<Long> listOfProjectOwners = projectRepository.findAll().stream()
                .map(Project::getOwnerId).toList();
        boolean isTheUserReal = listOfProjectOwners.contains(curatorId);
        return rolesContainCurator && isTheUserReal;
    }

    private boolean validationCompletedVacancy(VacancyDto vacancyDto) {
        int count = vacancyDto.getCount();
        List<Long> listTeamMemberIds = vacancyDto.getCandidateIds();
        List<TeamMember> listTeamMembers = listTeamMemberIds.stream()
                .map(memberId -> teamMemberRepository.findById(memberId))
                .filter(teamMember -> teamMember.getRoles().contains(ROLE_CANDIDATE))
                .toList();
        return listTeamMembers.size() >= count;
    }
}
