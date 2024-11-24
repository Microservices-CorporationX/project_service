package faang.school.projectservice.validator;

import faang.school.projectservice.exceptions.FileSizeExceededException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator {

    public void validateFileSize(MultipartFile file, long maxAllowedSize) {
        if (file.getSize() > maxAllowedSize) {
            throw new FileSizeExceededException(
                    String.format("File size (%d) exceeded maximum allowed (%d)",
                            file.getSize(), maxAllowedSize));
        }
    }
}
