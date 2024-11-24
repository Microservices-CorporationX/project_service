package faang.school.projectservice.utils.image;

import faang.school.projectservice.exceptions.ImageProcessingException;
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
public class ImageUtils {

    public BufferedImage getResizedBufferedImage(MultipartFile file, int maxWidth, int maxHeight) {
        BufferedImage originalImage = getBufferedOriginalImage(file);
        return resize(originalImage, maxWidth, maxHeight);
    }

    public InputStream getBufferedImageInputStream(MultipartFile originalImage, BufferedImage resizedImage) {
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
                Image resultingImage = originalImage.getScaledInstance(maxDimension, maxDimension, Image.SCALE_DEFAULT);
                finalImage = new BufferedImage(maxDimension, maxDimension, BufferedImage.TYPE_INT_BGR);
                finalImage.getGraphics().drawImage(resultingImage, 0, 0, null);
                return finalImage;
            }
        } else {
            if (height > maxHeight && width > maxWidth) {
                Image resultingImage = originalImage.getScaledInstance(maxWidth, maxHeight, Image.SCALE_DEFAULT);
                finalImage = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_BGR);
                finalImage.getGraphics().drawImage(resultingImage, 0, 0, null);
                return finalImage;
            } else if (width > maxWidth) {
                Image resultingImage = originalImage.getScaledInstance(maxWidth, height, Image.SCALE_DEFAULT);
                finalImage = new BufferedImage(maxWidth, height, BufferedImage.TYPE_INT_BGR);
                finalImage.getGraphics().drawImage(resultingImage, 0, 0, null);
                return finalImage;
            } else if (height > maxHeight) {
                Image resultingImage = originalImage.getScaledInstance(width, maxHeight, Image.SCALE_DEFAULT);
                finalImage = new BufferedImage(width, maxHeight, BufferedImage.TYPE_INT_BGR);
                finalImage.getGraphics().drawImage(resultingImage, 0, 0, null);
                return finalImage;
            }
        }
        return originalImage;
    }
}
