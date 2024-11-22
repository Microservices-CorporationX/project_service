package faang.school.projectservice.service.project;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ImageResizer {
    private static final int MAX_WIDTH_HORIZONTAL = 1080;
    private static final int MAX_HEIGHT_HORIZONTAL = 566;
    private static final int MAX_SIZE_SQUARE = 1080;

    public void resizeImage(MultipartFile image) {

        try {
            BufferedImage bufferedImage = Thumbnails.of(image.getInputStream()).asBufferedImage();

            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            if (width == height) {
                if (width > MAX_SIZE_SQUARE) {
                    bufferedImage = Thumbnails.of(image.getInputStream()).size(MAX_SIZE_SQUARE, MAX_SIZE_SQUARE).asBufferedImage();
                }
            } else {
                if (width > MAX_WIDTH_HORIZONTAL || height > MAX_HEIGHT_HORIZONTAL) {
                    bufferedImage = Thumbnails.of(image.getInputStream()).size(MAX_WIDTH_HORIZONTAL, MAX_HEIGHT_HORIZONTAL).asBufferedImage();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error resizing image", e);
        }
    }
}
