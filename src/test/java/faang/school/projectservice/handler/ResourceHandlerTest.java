package faang.school.projectservice.handler;

import faang.school.projectservice.exception.UnsupportedResourceException;
import faang.school.projectservice.validator.ResourceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceHandlerTest {
    @Mock
    private ResourceValidator resourceValidator;

    @InjectMocks
    private ResourceHandler resourceHandler;

    private MockMultipartFile validImageFile;
    private MockMultipartFile invalidImageFile;
    private BufferedImage bufferedImage;
    private int maxImageWidth;
    private int maxImageHeight;

    @BeforeEach
    void setUp() throws IOException {
        maxImageWidth = ResourceValidator.MAX_COVER_WIDTH_PX;
        maxImageHeight = ResourceValidator.MAX_COVER_HEIGHT_PX;

        bufferedImage = new BufferedImage(maxImageWidth, maxImageWidth, BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();

        validImageFile = new MockMultipartFile(
                "file",
                "validImage.jpg",
                "image/jpeg",
                new ByteArrayInputStream(imageBytes)
        );

        invalidImageFile = new MockMultipartFile(
                "file",
                "invalidImage.txt",
                "text/plain",
                new byte[]{1, 2, 3}
        );
    }

    @Test
    void testGetImageFromMultipartFile_ValidImage() {
        BufferedImage image = resourceHandler.getImageFromMultipartFile(validImageFile);

        assertThat(image.getWidth()).isEqualTo(bufferedImage.getWidth());
        assertThat(image.getHeight()).isEqualTo(bufferedImage.getHeight());
    }

    @Test
    void testGetImageFromMultipartFile_InvalidImage() {
        assertThatThrownBy(() -> resourceHandler.getImageFromMultipartFile(invalidImageFile))
                .isInstanceOf(UnsupportedResourceException.class)
                .hasMessageContaining(
                        String.format(
                                "Uploaded file '%s' is not a valid image.",
                                invalidImageFile.getOriginalFilename()));
    }

    @Test
    void testResizeImage_SquareImageWithinLimits() {
        when(resourceValidator.isSquareImage(bufferedImage)).thenReturn(true);

        BufferedImage resizedImage = resourceHandler.resizeImage(bufferedImage, maxImageWidth, maxImageWidth);

        assertThat(resizedImage.getWidth()).isEqualTo(maxImageWidth);
        assertThat(resizedImage.getHeight()).isEqualTo(maxImageWidth);
    }

    @Test
    void testResizeImage_NonSquareImageWithinLimits() {
        BufferedImage nonSquareImage = new BufferedImage(
                maxImageWidth,
                maxImageHeight,
                BufferedImage.TYPE_INT_RGB);
        when(resourceValidator.isSquareImage(nonSquareImage)).thenReturn(false);

        BufferedImage resizedImage = resourceHandler.resizeImage(nonSquareImage, maxImageWidth, maxImageHeight);

        assertThat(resizedImage.getWidth()).isEqualTo(maxImageWidth);
        assertThat(resizedImage.getHeight()).isLessThanOrEqualTo(maxImageHeight);
    }

    @Test
    void testResizeImage_SquareImageExceedsLimits() {
        BufferedImage invalidSquareImage = new BufferedImage(
                maxImageWidth + 1,
                maxImageWidth + 1,
                BufferedImage.TYPE_INT_RGB);
        when(resourceValidator.isSquareImage(invalidSquareImage)).thenReturn(true);

        BufferedImage resizedImage = resourceHandler.resizeImage(invalidSquareImage, maxImageWidth, maxImageWidth);

        assertThat(resizedImage.getWidth()).isEqualTo(maxImageWidth);
        assertThat(resizedImage.getHeight()).isEqualTo(maxImageWidth);
    }

    @Test
    void testResizeImage_NonSquareImageExceedsLimits() {
        BufferedImage invalidNonSquareImage = new BufferedImage(
                maxImageWidth + 1,
                maxImageHeight + 1,
                BufferedImage.TYPE_INT_RGB);
        when(resourceValidator.isSquareImage(invalidNonSquareImage)).thenReturn(false);

        BufferedImage resizedImage = resourceHandler.resizeImage(invalidNonSquareImage, maxImageWidth, maxImageHeight);

        assertThat(resizedImage.getWidth()).isEqualTo(maxImageWidth);
        assertThat(resizedImage.getHeight()).isLessThanOrEqualTo(maxImageHeight);
    }

    @Test
    void testResizeImage_NonSquareVerticalImageExceedsLimits() {
        BufferedImage invalidNonSquareImage = new BufferedImage(
                maxImageHeight + 1,
                maxImageWidth + 1,
                BufferedImage.TYPE_INT_RGB);
        when(resourceValidator.isSquareImage(invalidNonSquareImage)).thenReturn(false);

        BufferedImage resizedImage = resourceHandler.resizeImage(invalidNonSquareImage, maxImageWidth, maxImageHeight);

        assertThat(resizedImage.getWidth()).isLessThanOrEqualTo(maxImageWidth);
        assertThat(resizedImage.getHeight()).isLessThanOrEqualTo(maxImageHeight);
    }

    @Test
    void testConvertImageToMultipartFile_ValidImage() {
        MultipartFile convertedFile = resourceHandler.convertImageToMultipartFile(validImageFile, bufferedImage);

        assertThat(convertedFile).isNotNull();
        assertEquals(validImageFile.getName(), convertedFile.getName());
        assertEquals(validImageFile.getOriginalFilename(), convertedFile.getOriginalFilename());
        assertEquals(validImageFile.getContentType(), convertedFile.getContentType());
    }
}
