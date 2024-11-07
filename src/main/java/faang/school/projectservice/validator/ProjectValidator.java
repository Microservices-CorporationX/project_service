package faang.school.projectservice.validator;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.NotUniqueProjectException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectValidator {
    private final ProjectRepository projectRepository;

    public void validateUniqueProject(ProjectDto dto) {
        Long ownerId = dto.getOwnerId();
        String name = dto.getName();

        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            log.error("Project '{}' with ownerId #{} already exists.", name, ownerId);
            throw new NotUniqueProjectException("Project '" + name + "' with ownerId #" + ownerId + " already exists.");
        }
        log.info("Project '{}' with ownerId #{} unique and can be created.", name, ownerId);
    }
}