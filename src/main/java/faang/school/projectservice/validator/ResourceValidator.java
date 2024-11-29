package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EmptyResourceException;
import faang.school.projectservice.exception.UnsupportedResourceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

@Slf4j
@Component
public class ResourceValidator {
    public static final int MAX_COVER_SIZE_BYTES = 5 * 1024 * 1024;
    public static final int MAX_COVER_WIDTH_PX = 1080;
    public static final int MAX_COVER_HEIGHT_PX = 566;

    public void validateProjectCoverSize(MultipartFile file) {
        long coverSize = file.getSize();

        if (coverSize > MAX_COVER_SIZE_BYTES) {
            throw new UnsupportedResourceException(
                    String.format("File '%s' exceeds the maximum allowed size of '%d' MB.",
                            file.getOriginalFilename(), MAX_COVER_SIZE_BYTES / (1024 * 1024)));
        }

        log.info("File {} size is valid: {} bytes.", file.getOriginalFilename(), file.getSize());
    }

    public boolean isCorrectProjectCoverScale(BufferedImage image) {
        if (isSquareImage(image)) {
            return image.getWidth() <= MAX_COVER_WIDTH_PX;
        } else {
            return (image.getWidth() <= MAX_COVER_WIDTH_PX && image.getHeight() <= MAX_COVER_HEIGHT_PX);
        }
    }

    public boolean isSquareImage(BufferedImage image) {
        return image.getWidth() == image.getHeight();
    }

    public void validateResourceNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyResourceException(String.format("File '%s' is empty. It cannot be uploaded.",
                    file.getOriginalFilename()));
        }
    }
}