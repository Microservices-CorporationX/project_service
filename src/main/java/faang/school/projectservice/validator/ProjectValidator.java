package faang.school.projectservice.validator;


import faang.school.projectservice.dto.Project.ProjectDto;
import faang.school.projectservice.exception.NotUniqueProjectException;
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

    public void validateUniqueProject(ProjectDto dto) {
        Long ownerId = dto.getOwnerId();
        String name = dto.getName();

        if (projectRepository.existsByOwnerIdAndName(ownerId, name)) {
            log.error("Project '{}' with ownerId #{} already exists.", name, ownerId);

            throw new NotUniqueProjectException (String.format("Project '%s' with ownerId #%d already exists.",
                    name, ownerId));
        }
        log.info("Project '{}' with ownerId #{} unique and can be created.", name, ownerId);

    }

    public boolean canUserAccessProject (Project project, Long currentUserId) {
        return (project.getOwnerId().equals(currentUserId) || project.getVisibility() == ProjectVisibility.PUBLIC);
    }
}
