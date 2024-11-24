package faang.school.projectservice.validator.resource;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Slf4j
@Component
public class ResourceValidator {
    public void checkProjectStorageSizeExceeded(BigInteger updateProjectStorageSize, Project project) {
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

    public void checkUserInProject(@Positive long userId, TeamMember teamMember, Project project) {
        String messageError = "You can't delete this resource";
        if (teamMember.getUserId() == userId) {
            return;
        }
        if (teamMember.getRoles().contains(TeamRole.MANAGER)) {
            return;
        }
        if (userId == project.getOwnerId()) {
            return;
        }
        log.error("User id={} can't delete resource id={}", userId, teamMember.getId());
        throw new IllegalArgumentException(messageError);
    }
}
