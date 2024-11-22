package faang.school.projectservice.validator.resource;

import faang.school.projectservice.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Slf4j
@Component
public class ResourceValidator {
    public void validateProjectStorageSizeExceeded(BigInteger updateProjectStorageSize, Project project) {
        if (updateProjectStorageSize.compareTo(project.getMaxStorageSize()) > 0) {
            log.error("Project id={} has exceeded its max storage size:" +
                            " updateProjectStorageSize={}, projectMaxStorageSize={}",
                    project.getId(),
                    updateProjectStorageSize,
                    project.getMaxStorageSize()
            );
            throw new IllegalArgumentException("Project has exceeded max storage size");
        }
    }
}
