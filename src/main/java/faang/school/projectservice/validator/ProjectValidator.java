package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectValidator {
    private final ProjectJpaRepository projectRepository;

    public void validateUniqueProject(String name, Long ownerId) {
        if (projectRepository.existsByOwnerIdAndName(ownerId, name)) {
            log.error("Project '{}' with ownerId #{} already exists.", name, ownerId);
            throw new EntityNotFoundException("Project '" + name + "' with ownerId #" + ownerId + " already exists.");
        }
        log.info("Project '{}' with ownerId #{} does not exist. Can be created.", name, ownerId);
    }
}
