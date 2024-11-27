package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EmptyResourceException;
import faang.school.projectservice.exception.InsufficientStorageException;
import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class ResourceValidator {

    public void validateResourceNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyResourceException(String.format("File %s is empty. It cannot be uploaded", file.getName()));
        }
    }

    public void validateEnoughSpaceInStorage(Project project, MultipartFile file) {
        BigInteger availableStorageSize = project.getMaxStorageSize().subtract(project.getStorageSize());
        BigInteger neededStorageSize = BigInteger.valueOf(file.getSize());

        if (availableStorageSize.compareTo(neededStorageSize) < 0) {
            throw new InsufficientStorageException("Not enough space to store files");
        }
    }
}
