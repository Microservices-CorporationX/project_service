package faang.school.projectservice.validation.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private final ProjectRepository projectRepository;
    private final VacancyRepository vacancyRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void validateVacancyFields(VacancyDto vacancyDto) {
        if (vacancyDto.getCuratorId() == null) {
            throw new DataValidationException("Vacancy must have a curator");
        }
        if (vacancyDto.getName() == null || vacancyDto.getName().isBlank()) {
            throw new DataValidationException("Vacancy must have a name");
        }
        if (vacancyDto.getProjectId() == null) {
            throw new DataValidationException("Vacancy must be assigned to a project");
        }
        if (vacancyDto.getDescription() == null || vacancyDto.getDescription().isBlank()) {
            throw new DataValidationException("Vacancy must have a description");
        }
    }

    public void validateIfVacancyCanBeClosed(VacancyDto vacancyDto) {
        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());
        List<TeamMember> teamMembers = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(member -> vacancyDto.getCandidatesIds().contains(member.getUserId()))
                .toList();

        teamMembers.forEach(teamMember -> {
            if (teamMember.getRoles() == null || teamMember.getRoles().isEmpty()) {
                throw new DataValidationException("Vacancy can't be closed until all team members got their roles");
            }
        });
    }

    public void validateIfCandidatesNoMoreNeeded(VacancyDto vacancyDto) {
        long vacancyId = vacancyDto.getId();
        int workersRequiredQuantity = vacancyDto.getWorkersRequired();
        Vacancy vacancy = vacancyRepository.findById(vacancyDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Vacancy doesn't exist by id: " + vacancyId));
        long candidatesApprovedQuantity = vacancy.getCandidates().stream()
                .filter(candidate -> candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .count();

        if (candidatesApprovedQuantity <= workersRequiredQuantity) {
            throw new DataValidationException(
                    String.format("Vacation can't be closed until %d candidates accepted", workersRequiredQuantity));
        }
    }

    public void validateIfProjectExistsById(long projectId) {
        projectRepository.getProjectById(projectId);
    }

    public void validateCuratorRole(long userId) {
        TeamMember curator = teamMemberRepository.findById(userId);
        List<TeamRole> curatorRole = curator.getRoles().stream()
                .filter(role -> role.equals(TeamRole.MANAGER) || role.equals(TeamRole.OWNER))
                .toList();
        if (curatorRole.isEmpty()) {
            throw new DataValidationException("Curator hasn't got required role for creating a vacancy");
        }
    }
}
