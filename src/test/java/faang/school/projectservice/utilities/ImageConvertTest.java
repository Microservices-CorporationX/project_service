package faang.school.projectservice.utilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
class ImageConvertTest {

    @InjectMocks
    private ImageConvert imageConvert;
    private BufferedImage resultBufferedImage;
    int testResultImageSize = 1000;
    String imageType = "jpg";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageConvert, "typeImage", imageType);
    }

    @Test
    void testResizeImageJpgWidthMoreThanHeightSuccess() {
        imageConvertTest(100, 30);

        assertEquals(300, resultBufferedImage.getHeight());
        assertEquals(1000, resultBufferedImage.getWidth());
    }

    @Test
    void testResizeImageJpgWidthMoreThanHeightFail() {
        imageConvertTest(100, 30);

        assertNotEquals(1000, resultBufferedImage.getHeight());
        assertNotEquals(300, resultBufferedImage.getWidth());
    }

    @Test
    void testResizeImageJpgHeightMoreThanWidthSuccess() {
        imageConvertTest(20, 100);

        assertEquals(1000, resultBufferedImage.getHeight());
        assertEquals(200, resultBufferedImage.getWidth());
    }

    @Test
    void testResizeImageJpgHeightMoreThanWidthFail() {
        imageConvertTest(20, 100);

        assertNotEquals(200, resultBufferedImage.getHeight());
        assertNotEquals(1000, resultBufferedImage.getWidth());
    }

    private void imageConvertTest(int testWidth, int testHeight) {
        BufferedImage bufferedImage = new BufferedImage(testWidth, testHeight, BufferedImage.TYPE_INT_RGB);
        InputStream resultImageStream;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, imageType, byteArrayOutputStream);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            resultImageStream = imageConvert.resizeImageJpg(byteArrayInputStream, testResultImageSize);

            resultBufferedImage = ImageIO.read(resultImageStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}