package faang.school.projectservice.utils.image;

import faang.school.projectservice.exceptions.ImageProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class ImageUtils {

    public BufferedImage getResizedBufferedImage(MultipartFile file, int maxWidth, int maxHeight) {
        log.debug("Trying to get resized BufferedImage from MultiPartFile");
        BufferedImage originalImage = getBufferedOriginalImage(file);
        return resize(originalImage, maxWidth, maxHeight);
    }

    public InputStream getBufferedImageInputStream(MultipartFile originalImage, BufferedImage resizedImage) {
        log.debug("Trying to get BufferedImage InputStream");

        String fileName = originalImage.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, extension, outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            throw new ImageProcessingException("An error occurred when converting resized image to an InputStream");
        }
    }

    private BufferedImage getBufferedOriginalImage(MultipartFile file) {
        try {
            return ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new ImageProcessingException("An error occurred when converting MultiPartFile to BufferedImage");
        }
    }

    private BufferedImage resize(BufferedImage originalImage, int maxWidth, int maxHeight) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage finalImage;
        if (height == width) {
            int maxDimension = Math.max(maxWidth, maxHeight);

            if (height > maxDimension) {
                width = maxDimension;
                height = maxDimension;
            }
        } else {
            if (height > maxHeight && width > maxWidth) {
                width = maxWidth;
                height = maxHeight;
            } else if (width > maxWidth) {
                width = maxWidth;
            } else if (height > maxHeight) {
                height = maxHeight;
            }
        }
        Image resultingImage = originalImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        finalImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return finalImage;
    }
}
