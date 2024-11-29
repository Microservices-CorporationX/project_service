package faang.school.projectservice.service.image;

import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class ImageService {

    private static final int MAX_FIRST_SIZE_IMAGE = 1080;
    private static final int MAX_SECOND_SIZE_IMAGE = 566;

    public BufferedImage resizeImage(BufferedImage image) {

        int height = image.getHeight();
        int width = image.getWidth();
        if (height == width) {
            height = MAX_FIRST_SIZE_IMAGE;
            width = MAX_FIRST_SIZE_IMAGE;
        } else {
            double delta = 0.0;
            if (height > width) {
                delta = Math.max(
                        (double) width / MAX_SECOND_SIZE_IMAGE,
                        (double) height / MAX_FIRST_SIZE_IMAGE
                );
            }
            if (height < width) {
                delta = Math.max(
                        (double) width / MAX_FIRST_SIZE_IMAGE,
                        (double) height / MAX_SECOND_SIZE_IMAGE
                );
            }
            width = (int) (width / delta);
            height = (int) (height / delta);
        }

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(image, 0, 0, width, height, null);

        return outputImage;
    }
}