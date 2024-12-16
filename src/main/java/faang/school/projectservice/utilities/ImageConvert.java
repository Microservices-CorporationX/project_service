package faang.school.projectservice.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ImageConvert {
    @Value("${file.extension.type}")
    private String typeImage;

    public InputStream resizeImageJpg(InputStream imageInputStream, int targetImageSize) throws IOException {

        BufferedImage originalBufferedImage = ImageIO.read(imageInputStream);

        int heightNewImage;
        int widthNewImage;

        if (originalBufferedImage.getHeight() >= originalBufferedImage.getWidth()) {
            heightNewImage = targetImageSize;
            widthNewImage = (int) ((double) originalBufferedImage.getWidth()
                    * targetImageSize
                    / originalBufferedImage.getHeight());
        } else {
            widthNewImage = targetImageSize;
            heightNewImage = (int) ((double) originalBufferedImage.getHeight()
                    * targetImageSize
                    / originalBufferedImage.getWidth());
        }

        BufferedImage resizedImage = new BufferedImage(widthNewImage, heightNewImage, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalBufferedImage, 0, 0, widthNewImage, heightNewImage, null);
        graphics2D.dispose();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, typeImage, byteArrayOutputStream);
        byteArrayOutputStream.flush();
        byte[] imageInByte = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();

        return new ByteArrayInputStream(imageInByte);
    }
}