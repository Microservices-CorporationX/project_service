package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.InsufficientCandidatesException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private static final List<TeamRole> ROLES_TO_MANAGE_VACANCY = List.of(TeamRole.OWNER, TeamRole.MANAGER);
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;

    public void validateProjectInVacancyExists(VacancyDto dto) {
        if (!projectService.checkProjectExistsById(dto.getProjectId())) {
            throw new EntityNotFoundException(String.format("Project doesn't exist by id: %s", dto.getProjectId()));
        }
    }

    public void validateVacancyCreatorRole(VacancyDto dto) {
        TeamMember creator = teamMemberService.getTeamMemberById(dto.getCreatedBy());
        if (creator.getRoles().stream().noneMatch((ROLES_TO_MANAGE_VACANCY::contains))) {
            throw new DataValidationException("Vacancy can be created by following roles " + ROLES_TO_MANAGE_VACANCY);
        }
    }

    public void validateVacancyUpdaterRole(VacancyDto dto) {
        TeamMember updater = teamMemberService.getTeamMemberById(dto.getUpdatedBy());
        if (updater.getRoles().stream().noneMatch((ROLES_TO_MANAGE_VACANCY::contains))) {
            throw new DataValidationException("Vacancy status can be modified by following roles " + ROLES_TO_MANAGE_VACANCY);
        }
    }

    public void validateCandidateCountForClosure(Vacancy vacancy) {
        if (vacancy.getCandidates().size() < vacancy.getCount()) {
            throw new InsufficientCandidatesException(
                    "Vacancy should have at least " + vacancy.getCount() + " candidates to be closed");
        }
    }
}
