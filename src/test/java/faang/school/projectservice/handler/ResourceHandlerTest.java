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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceHandlerTest {
    @Mock
    private ResourceValidator resourceValidator;

    @InjectMocks
    private ResourceHandler resourceHandler;

    private MockMultipartFile validSquareImageFile;
    private MockMultipartFile validNonSquareImageFile;
    private MockMultipartFile invalidImageFile;
    private BufferedImage squareImage;
    private BufferedImage nonSquareImage;
    private int maxCoverWidth;
    private int maxCoverHeight;

    @BeforeEach
    void setUp() throws IOException {
        maxCoverWidth = ResourceValidator.MAX_COVER_WIDTH_PX;
        maxCoverHeight = ResourceValidator.MAX_COVER_HEIGHT_PX;

        squareImage = new BufferedImage(maxCoverWidth, maxCoverWidth, BufferedImage.TYPE_INT_RGB);
        nonSquareImage = new BufferedImage(maxCoverWidth, maxCoverHeight, BufferedImage.TYPE_INT_RGB);

        byte[] squareImageBytes = convertImageToByteArray(squareImage, "jpg");
        byte[] nonSquareImageBytes = convertImageToByteArray(nonSquareImage, "jpg");

        validSquareImageFile = new MockMultipartFile(
                "file",
                "validSquareImage.jpg",
                "image/jpeg",
                new ByteArrayInputStream(squareImageBytes)
        );

        validNonSquareImageFile = new MockMultipartFile(
                "file",
                "validNonSquareImage.jpg",
                "image/jpeg",
                new ByteArrayInputStream(nonSquareImageBytes)
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
        BufferedImage image = resourceHandler.getImageFromMultipartFile(validSquareImageFile);

        assertThat(image.getWidth()).isEqualTo(squareImage.getWidth());
        assertThat(image.getHeight()).isEqualTo(squareImage.getHeight());
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
        when(resourceValidator.isSquareImage(squareImage)).thenReturn(true);

        BufferedImage resizedImage = resourceHandler.resizeImage(squareImage, maxCoverWidth, maxCoverWidth);

        assertThat(resizedImage.getWidth()).isEqualTo(maxCoverWidth);
        assertThat(resizedImage.getHeight()).isEqualTo(maxCoverWidth);
    }

    @Test
    void testResizeImage_NonSquareImageWithinLimits() {
        BufferedImage nonSquareImage = new BufferedImage(
                maxCoverWidth,
                maxCoverHeight,
                BufferedImage.TYPE_INT_RGB);
        when(resourceValidator.isSquareImage(nonSquareImage)).thenReturn(false);

        BufferedImage resizedImage = resourceHandler.resizeImage(nonSquareImage, maxCoverWidth, maxCoverHeight);

        assertThat(resizedImage.getWidth()).isEqualTo(maxCoverWidth);
        assertThat(resizedImage.getHeight()).isLessThanOrEqualTo(maxCoverHeight);
    }

    @Test
    void testResizeImage_SquareImageExceedsLimits() {
        BufferedImage invalidSquareImage = new BufferedImage(
                maxCoverWidth + 1,
                maxCoverWidth + 1,
                BufferedImage.TYPE_INT_RGB);
        when(resourceValidator.isSquareImage(invalidSquareImage)).thenReturn(true);

        BufferedImage resizedImage = resourceHandler.resizeImage(invalidSquareImage, maxCoverWidth, maxCoverWidth);

        assertThat(resizedImage.getWidth()).isEqualTo(maxCoverWidth);
        assertThat(resizedImage.getHeight()).isEqualTo(maxCoverWidth);
    }

    @Test
    void testResizeImage_NonSquareImageExceedsLimits() {
        BufferedImage invalidNonSquareImage = new BufferedImage(
                maxCoverWidth + 1,
                maxCoverHeight + 1,
                BufferedImage.TYPE_INT_RGB);
        when(resourceValidator.isSquareImage(invalidNonSquareImage)).thenReturn(false);

        BufferedImage resizedImage = resourceHandler.resizeImage(invalidNonSquareImage, maxCoverWidth, maxCoverHeight);

        assertThat(resizedImage.getWidth()).isEqualTo(maxCoverWidth);
        assertThat(resizedImage.getHeight()).isLessThanOrEqualTo(maxCoverHeight);
    }

    @Test
    void testResizeImage_NonSquareVerticalImageExceedsLimits() {
        BufferedImage invalidNonSquareImage = new BufferedImage(
                maxCoverHeight + 1,
                maxCoverWidth + 1,
                BufferedImage.TYPE_INT_RGB);
        when(resourceValidator.isSquareImage(invalidNonSquareImage)).thenReturn(false);

        BufferedImage resizedImage = resourceHandler.resizeImage(invalidNonSquareImage, maxCoverWidth, maxCoverHeight);

        assertThat(resizedImage.getWidth()).isLessThanOrEqualTo(maxCoverWidth);
        assertThat(resizedImage.getHeight()).isLessThanOrEqualTo(maxCoverHeight);
    }

    @Test
    void testConvertImageToMultipartFile_ValidImage() {
        MultipartFile convertedFile = resourceHandler.convertImageToMultipartFile(validSquareImageFile, squareImage);

        assertThat(convertedFile).isNotNull();
        assertEquals(validSquareImageFile.getName(), convertedFile.getName());
        assertEquals(validSquareImageFile.getOriginalFilename(), convertedFile.getOriginalFilename());
        assertEquals(validSquareImageFile.getContentType(), convertedFile.getContentType());
    }

    @Test
    void testHandleImage_SuccessWithoutResize() throws IOException {
        when(resourceValidator.isCorrectProjectCoverScale(any())).thenReturn(true);

        MultipartFile result = resourceHandler.handleImage(validNonSquareImageFile);

        verify(resourceValidator, times(1)).isCorrectProjectCoverScale(any());
        verify(resourceValidator, never()).isSquareImage(any());
        assertThat(result.getOriginalFilename()).isEqualTo(validNonSquareImageFile.getOriginalFilename());
        assertThat(result.getContentType()).isEqualTo(validNonSquareImageFile.getContentType());
        assertThat(result.getBytes()).isEqualTo(validNonSquareImageFile.getBytes());
    }

    @Test
    void testHandleImage_SuccessWithResize() throws IOException {
        when(resourceValidator.isCorrectProjectCoverScale(any())).thenReturn(false);

        MultipartFile result = resourceHandler.handleImage(validNonSquareImageFile);

        verify(resourceValidator, times(1)).isCorrectProjectCoverScale(any());
        verify(resourceValidator, times(1)).isSquareImage(any());
        assertThat(result.getOriginalFilename()).isEqualTo(validNonSquareImageFile.getOriginalFilename());
        assertThat(result.getContentType()).isEqualTo(validNonSquareImageFile.getContentType());
        assertThat(result.getBytes()).isEqualTo(validNonSquareImageFile.getBytes());
    }

    private byte[] convertImageToByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();
        return imageBytes;
    }
}
