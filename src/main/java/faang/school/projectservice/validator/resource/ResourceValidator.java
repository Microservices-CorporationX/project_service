package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.StorageExceededException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class ResourceValidator {

    public void validateAllowedToDeleteFile(Resource resource, TeamMember teamMember) {
        if (!resource.getCreatedBy().getId().equals(teamMember.getId())
                && teamMember.getRoles().stream()
                .anyMatch(teamRole -> teamRole.equals(TeamRole.MANAGER))) {
            throw new DataValidationException("Delete file allowed only creator or manager");
        }
    }

    public void checkMaxStorageSizeIsNotNull(BigInteger maxStorageSize) {
        if (maxStorageSize == null) {
            throw new IllegalStateException("Max storage size is not set for the project.");
        }
    }

    public void checkStorageSizeNotExceeded(BigInteger maxStorageSize,
                                             BigInteger currentStorageSize) {
        if (maxStorageSize.compareTo(currentStorageSize) < 0) {
            throw new StorageExceededException("Storage can't exceed 2 Gb ");
        }
    }
}
