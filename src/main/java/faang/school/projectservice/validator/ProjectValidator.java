package faang.school.projectservice.validator;

import faang.school.projectservice.exeption.NotUniqueProjectException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void validateUniqueProject(Project project) {
        Long ownerId = project.getOwnerId();
        String name = project.getName();

        if (projectRepository.existsByOwnerIdAndName(ownerId, name)) {
            log.error("Project '{}' with ownerId #{} already exists.", name, ownerId);

            throw new NotUniqueProjectException(String.format("Project '%s' with ownerId #%d already exists.",
                    name, ownerId));
        }
        log.info("Project '{}' with ownerId #{} unique and can be created.", name, ownerId);
    }

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