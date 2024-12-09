package faang.school.projectservice.validator;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class CoverProjectValidator {
    private final UserContext userContext;

    public void validation(Project project) {
        checkUserOnTeam(project);
    }

    public void validation(Project project, MultipartFile coverImage) {
        checkUserOnTeam(project);

        if (coverImage == null || coverImage.isEmpty()) {
            throw new IllegalStateException("The cover image is empty");
        }

        if (!coverImage.getContentType().startsWith("image")) {
            throw new IllegalStateException("Expected is image");
        }
    }

    private void checkUserOnTeam(Project project) {
        boolean isExists = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getUserId().equals(userContext.getUserId()));

        if (!isExists) {
            throw new IllegalStateException("The user must be on the team");
        }
    }
}
