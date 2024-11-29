package faang.school.projectservice.utils.image;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageUtilsTest {

    private ImageUtils imageUtils;

    private MultipartFile file;
    private int maxWidth;
    private int maxHeight;

    @BeforeEach
    public void setUp() {
        imageUtils = new ImageUtils();

        maxWidth = 1080;
        maxHeight = 566;
        file = Mockito.mock(MultipartFile.class);
    }

    @Test
    void testGetResizedBufferedImage_NoResize() throws IOException {
        // arrange
        int imageWidth = 560;
        int imageHeight = 560;
        BufferedImage originalImage = new BufferedImage(
                imageWidth,
                imageHeight,
                BufferedImage.TYPE_INT_RGB
        );

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", byteArrayOutputStream);

        ByteArrayInputStream inputStream
                = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        when(file.getInputStream()).thenReturn(inputStream);
        when(file.getOriginalFilename()).thenReturn("image.jpg");

        // act
        BufferedImage resizedImage = imageUtils.getResizedBufferedImage(
                file,
                maxWidth,
                maxHeight
        );

        // assert
        assertEquals(imageWidth, resizedImage.getWidth());
        assertEquals(imageHeight, resizedImage.getHeight());
    }

    @Test
    void testGetResizedBufferedImage_ResizeSquareImage() throws IOException {
        // arrange
        int imageWidth = 2000;
        int imageHeight = 2000;
        BufferedImage originalImage = new BufferedImage(
                imageWidth,
                imageHeight,
                BufferedImage.TYPE_INT_RGB
        );

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", byteArrayOutputStream);

        ByteArrayInputStream inputStream
                = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        when(file.getInputStream()).thenReturn(inputStream);
        when(file.getOriginalFilename()).thenReturn("image.jpg");

        // act
        BufferedImage resizedImage = imageUtils.getResizedBufferedImage(
                file,
                maxWidth,
                maxHeight
        );

        // assert
        assertEquals(maxWidth, resizedImage.getWidth());
        assertEquals(maxWidth, resizedImage.getHeight());
    }

    @Test
    void testGetResizedBufferedImage_ResizeWidth() throws IOException {
        // arrange
        int imageWidth = 2000;
        int imageHeight = 500;
        BufferedImage originalImage = new BufferedImage(
                imageWidth,
                imageHeight,
                BufferedImage.TYPE_INT_RGB
        );

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", byteArrayOutputStream);

        ByteArrayInputStream inputStream
                = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        when(file.getInputStream()).thenReturn(inputStream);
        when(file.getOriginalFilename()).thenReturn("image.jpg");

        // act
        BufferedImage resizedImage = imageUtils.getResizedBufferedImage(
                file,
                maxWidth,
                maxHeight
        );

        // assert
        assertEquals(maxWidth, resizedImage.getWidth());
        assertEquals(imageHeight, resizedImage.getHeight());
    }

    @Test
    void testGetResizedBufferedImage_ResizeHeight() throws IOException {
        // arrange
        int imageWidth = 500;
        int imageHeight = 2000;
        BufferedImage originalImage = new BufferedImage(
                imageWidth,
                imageHeight,
                BufferedImage.TYPE_INT_RGB
        );

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", byteArrayOutputStream);

        ByteArrayInputStream inputStream
                = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        when(file.getInputStream()).thenReturn(inputStream);
        when(file.getOriginalFilename()).thenReturn("image.jpg");

        // act
        BufferedImage resizedImage = imageUtils.getResizedBufferedImage(
                file,
                maxWidth,
                maxHeight
        );

        // assert
        assertEquals(imageWidth, resizedImage.getWidth());
        assertEquals(maxHeight, resizedImage.getHeight());
    }

    @Test
    public void testGetBufferedImageInputStream() {
        // arrange
        int imageWidth = 500;
        int imageHeight = 2000;
        BufferedImage resizedImage = new BufferedImage(
                imageWidth,
                imageHeight,
                BufferedImage.TYPE_INT_RGB
        );

        when(file.getOriginalFilename()).thenReturn("image.jpg");

        // act
        InputStream inputStream = imageUtils.getBufferedImageInputStream(file, resizedImage);

        // assert
        assertNotNull(inputStream);
    }
}
