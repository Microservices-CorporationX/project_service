package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.StorageExceededException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class ResourceValidator {

    @Value("${project-files.max-project-file-size}")
    long maxProjectFileSize;

    public void validateAllowedToDeleteFile(Resource resource, TeamMember teamMember) {
        if (!resource.getCreatedBy().getId().equals(teamMember.getId())
                && teamMember.getRoles().stream()
                .noneMatch(teamRole -> teamRole.equals(TeamRole.MANAGER))) {
            throw new DataValidationException("Delete file allowed only creator or manager");
        }
    }

    public void validateMaxStorageSizeIsNotNull(BigInteger maxStorageSize) {
        if (maxStorageSize == null) {
            throw new IllegalStateException("Max storage size is not set for the project.");
        }
    }

    public void validateStorageSizeNotExceeded(BigInteger maxStorageSize,
                                               BigInteger currentStorageSize) {
        if (maxStorageSize.compareTo(currentStorageSize) < 0) {
            throw new StorageExceededException("Storage can't exceed 2 Gb ");
        }
    }

    public void validateFileSizeNotBigger2Gb(Long fileSize) {
        if (fileSize > maxProjectFileSize) {
            throw new DataValidationException("Max uploading file size can't be more than 2GB");
        }
    }
}
