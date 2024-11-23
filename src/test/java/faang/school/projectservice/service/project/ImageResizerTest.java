package faang.school.projectservice.service.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ImageResizerTest {

    private ImageResizer imageResizer;

    private MultipartFile multipartFile;

    private String contentType;
    @BeforeEach
    public void setUp() {
        imageResizer = new ImageResizer();
        contentType = "image/png";
    }

    @Test
    public void testResizeHorizontal() throws IOException {
        byte[] bytes = createMockImage(1500, 800);
        multipartFile = new MockMultipartFile("image", "test.png", contentType, bytes);

        assertResult(bytes);
    }

    @Test
    public void testResizeSquare() throws IOException {
        byte[] bytes = createMockImage(1500, 1500);
        multipartFile = new MockMultipartFile("image", "test.png", contentType, bytes);

        assertResult(bytes);
    }

    private void assertResult(byte[] bytes) {
        ByteArrayOutputStream resultStream = imageResizer.resizeImage(multipartFile);

        assertNotNull(resultStream);
        assertNotEquals(bytes, resultStream.toByteArray());
    }


    private byte[] createMockImage(int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
