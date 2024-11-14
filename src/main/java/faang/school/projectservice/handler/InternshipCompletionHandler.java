package faang.school.projectservice.handler;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InternshipCompletionHandler {
    public void processInternshipCompletion(Internship internship, InternshipStatus status) {
        if (status == InternshipStatus.COMPLETED) {
            handleInternsCompletion(internship);
        }
    }

    public void internsToDismissal(List<@Positive Long> interns) {
        if (interns != null && !interns.isEmpty()) {
            interns.clear();
        }
    }

    private void handleInternsCompletion(Internship internship) {
        for (TeamMember intern : internship.getInterns()) {
            if (hasCompletedAllTasks(intern)) {
                assignNewRole(intern);
            } else {
                removeInternFromTeam(intern);
            }
        }
    }

    private boolean hasCompletedAllTasks(TeamMember intern) {
        return intern.getStages().stream()
                .flatMap(stage -> stage.getTasks().stream())
                .allMatch(task -> task.getStatus() == TaskStatus.DONE);
    }

    private void assignNewRole(TeamMember intern) {
        intern.getRoles().remove(TeamRole.INTERN);
        intern.getRoles().add(TeamRole.DEVELOPER);
    }

    private void removeInternFromTeam(TeamMember intern) {
        Team team = intern.getTeam();
        if (team != null) {
            team.getTeamMembers().remove(intern);
        }
    }
}
