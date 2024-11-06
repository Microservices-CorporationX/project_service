package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private static final List<TeamRole> ROLES_TO_CREATE_VACANCY = List.of(TeamRole.OWNER, TeamRole.MANAGER);
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;

    public void validateProjectInVacancyExists(VacancyDto dto) {
        if (!projectService.checkProjectExistsById(dto.getProjectId())) {
            throw new EntityNotFoundException(String.format("Project doesn't exist by id: %s", dto.getProjectId()));
        }
    }

    public void validateVacancyCreatorRole(VacancyDto dto) {
        TeamMember teamMember = teamMemberService.getTeamMemberById(dto.getCreatedBy());
        if(teamMember.getRoles().stream().noneMatch((ROLES_TO_CREATE_VACANCY::contains))) {
            throw new DataValidationException("Vacancy can be created by following roles " + ROLES_TO_CREATE_VACANCY);
        }
    }
}
