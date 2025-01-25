package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProjectValidator {

    public boolean canUserAccessProject(Project project, Long currentUserId) {
        return (project.getVisibility() == ProjectVisibility.PUBLIC || project.getOwnerId().equals(currentUserId)
                || project.getTeams()
                .stream()
                .flatMap(team -> team.getTeamMembers()
                        .stream())
                .anyMatch(teamMember -> teamMember.getUserId().equals(currentUserId))
        );
    }
}