package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;

    public Vacancy createVacancy(Vacancy vacancy) {
        Project project = projectService.getProjectById(vacancy.getProject().getId());
        vacancy.setProject(project);
        vacancy.setCreatedAt(LocalDateTime.now());
        vacancy.setUpdatedAt(null);
        vacancy.setUpdatedBy(null);
        vacancy.setStatus(VacancyStatus.OPEN);

        if (checkTutorRole(vacancy.getCreatedBy(), vacancy.getProject().getId())) {
            log.info("Vacancy created: " + vacancy);
            return vacancyRepository.save(vacancy);
        }

        throw new DataValidationException("%d user does not have permission to add a vacancy"
                .formatted(vacancy.getCreatedBy()));
    }

    private boolean checkTutorRole(Long id, Long projectId) {
        TeamMember teamMember = teamMemberService.getTeamMemberByIdAndProjectId(id, projectId);
        return teamMember.getRoles().contains(TeamRole.OWNER) || teamMember.getRoles().contains(TeamRole.MANAGER);
    }
}
