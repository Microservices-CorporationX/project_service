package faang.school.projectservice.validator.moment;

import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.exception.TeamMemberNotFoundException;
import faang.school.projectservice.exception.vacancy.DataValidationException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MomentServiceValidator {
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void validateProjectsExist(List<Long> projectIds) {
        projectIds.stream()
                .filter(projectId -> !projectRepository.existsById(projectId))
                .findFirst()
                .ifPresent(projectId -> {
                    throw new ProjectNotFoundException(projectId);
                });
    }

    public void validateProjectsAreActive(List<Long> projectIds) {
        List<ProjectStatus> activeStates = List.of(ProjectStatus.CREATED, ProjectStatus.IN_PROGRESS);
        for (Long projectId : projectIds) {
            ProjectStatus status = projectRepository.getProjectById(projectId).getStatus();
            if (!activeStates.contains(status)) {
                throw new DataValidationException("Project is not active. " +
                        "Current status for project with ID: " + projectId + " is: " + status +
                        " Following team mates belong to this project and therefore cannot be added to the moment : " + getTeamMemberIds(projectId));
            }
        }
    }

    private List<Long> getTeamMemberIds(Long projectId) {
        return projectRepository.getProjectById(projectId).getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream()
                        .map(TeamMember::getId)).toList();
    }

    public void validateListContainUniqueItems(List<Long> ids, String listName) {
        Set<Long> uniqueProjectIds = new HashSet<>(ids);
        if (ids.size() != uniqueProjectIds.size()) {
            throw new DataValidationException("List: " + listName + " has duplicate items");
        }
    }

    public void validateTeamMemberExists(List<Long> teamIds) {
        teamIds.forEach(teamId -> {
                    try {
                        teamMemberRepository.findById(teamId);
                    } catch (EntityNotFoundException e) {
                        throw new TeamMemberNotFoundException("Team member not found: " + teamId);
                    }
                }
        );
    }
}
