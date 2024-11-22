package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ResourceValidator {

    public void validateEnoughSpaceInStorage(Project project, List<MultipartFile> files) {
        BigInteger availableStorageSize = project.getStorageSize();
        BigInteger neededStorageSize = files.stream()
                .map(file -> BigInteger.valueOf(file.getSize()))
                .reduce(BigInteger.ZERO, (accumulator, element) -> accumulator.add(element));

//        if (neededStorageSize.compareTo(availableStorageSize) < 0) {
//            throw new InsufficientStorageException("Not enough space to store files");
//        }
    }
}
