package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EmptyResourceException;
import faang.school.projectservice.exception.UnsupportedResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceValidatorTest {
    private ResourceValidator resourceValidator;

    private MockMultipartFile validFile;
    private MockMultipartFile emptyFile;
    private MockMultipartFile oversizedFile;
    private BufferedImage image;
    private BufferedImage validSquareImage;
    private BufferedImage validNonSquareImage;

    private int maxFileSize;
    private int maxImageWidth;
    private int maxImageHeight;

    @BeforeEach
    void setUp() {
        resourceValidator = new ResourceValidator();
        maxFileSize = ResourceValidator.MAX_COVER_SIZE_BYTES;
        maxImageWidth = ResourceValidator.MAX_COVER_WIDTH_PX;
        maxImageHeight = ResourceValidator.MAX_COVER_HEIGHT_PX;

        validFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[maxFileSize - 1]
        );

        emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        oversizedFile = new MockMultipartFile(
                "file",
                "oversized.jpg",
                "image/jpeg",
                new byte[maxFileSize + 1]
        );

        validSquareImage = new BufferedImage(
                maxImageWidth,
                maxImageWidth,
                BufferedImage.TYPE_INT_RGB);

        validNonSquareImage = new BufferedImage(
                maxImageWidth,
                maxImageHeight,
                BufferedImage.TYPE_INT_RGB);
    }

    @Test
    void testValidateProjectCoverSize_ValidFile() {
        assertThatCode(() -> resourceValidator.validateProjectCoverSize(validFile))
                .doesNotThrowAnyException();
    }

    @Test
    void testValidateProjectCoverSize_FileTooLarge() {
        assertThatThrownBy(() -> resourceValidator.validateProjectCoverSize(oversizedFile))
                .isInstanceOf(UnsupportedResourceException.class)
                .hasMessageContaining(
                        String.format("File '%s' exceeds the maximum allowed size",
                        oversizedFile.getOriginalFilename()));
    }

    @Test
    void testIsCorrectProjectCoverScale_SquareImageWithinLimits() {
        assertThat(resourceValidator.isCorrectProjectCoverScale(validSquareImage)).isTrue();
    }

    @Test
    void testIsCorrectProjectCoverScale_SquareImageExceedsLimits() {
        image = new BufferedImage(
                maxImageWidth + 1,
                maxImageWidth + 1,
                BufferedImage.TYPE_INT_RGB);

        assertThat(resourceValidator.isCorrectProjectCoverScale(image)).isFalse();
    }

    @Test
    void testIsCorrectProjectCoverScale_NonSquareImageWithinLimits() {
        assertThat(resourceValidator.isCorrectProjectCoverScale(validNonSquareImage)).isTrue();
    }

    @Test
    void testIsCorrectProjectCoverScale_NonSquareImageExceedsHeight() {
        image = new BufferedImage(
                maxImageWidth,
                maxImageHeight + 1,
                BufferedImage.TYPE_INT_RGB);

        assertThat(resourceValidator.isCorrectProjectCoverScale(image)).isFalse();
    }

    @Test
    void testIsCorrectProjectCoverScale_NonSquareImageExceedsWidth() {
        image = new BufferedImage(
                maxImageWidth + 1,
                maxImageHeight,
                BufferedImage.TYPE_INT_RGB);

        assertThat(resourceValidator.isCorrectProjectCoverScale(image)).isFalse();
    }

    @Test
    void testIsSquareImage_SquareImage() {
        assertThat(resourceValidator.isSquareImage(validSquareImage)).isTrue();
    }

    @Test
    void testIsSquareImage_NonSquareImage() {
        assertThat(resourceValidator.isSquareImage(validNonSquareImage)).isFalse();
    }

    @Test
    void testValidateResourceNotEmpty_ValidFile() {
        assertThatCode(() -> resourceValidator.validateResourceNotEmpty(validFile))
                .doesNotThrowAnyException();
    }

    @Test
    void testValidateResourceNotEmpty_EmptyFile() {
        assertThatThrownBy(() -> resourceValidator.validateResourceNotEmpty(emptyFile))
                .isInstanceOf(EmptyResourceException.class)
                .hasMessageContaining(
                        String.format("File '%s' is empty. It cannot be uploaded.",
                                emptyFile.getOriginalFilename()));
    }
}