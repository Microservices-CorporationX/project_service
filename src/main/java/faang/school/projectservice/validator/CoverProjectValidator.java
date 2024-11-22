package faang.school.projectservice.validator;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoverProjectValidator {
    private final UserContext userContext;

    public void validation(Project project) {
        boolean isExists = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getUserId().equals(userContext.getUserId()));

        if (!isExists) {
            throw new IllegalStateException("The user must be on the team");
        }
    }
}
