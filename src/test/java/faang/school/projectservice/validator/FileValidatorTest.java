package faang.school.projectservice.validator;

import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.FileSizeExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileValidatorTest {

    private FileValidator fileValidator;

    private MockMultipartFile file;

    @BeforeEach
    public void setUp() {
        fileValidator = new FileValidator();

        file = new MockMultipartFile(
                "file",
                "file.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[5000]
        );
    }

    @Test
    public void testValidateFileSize() {
        // arrange
        long maxAllowedSize = 1024 * 5 * 5;

        // act and assert
        assertDoesNotThrow(() -> fileValidator.validateFileSize(file, maxAllowedSize));
    }

    @Test
    public void testValidateFileSizeFails() {
        // arrange
        long maxAllowedSize = 2500;

        // act
        assertThrows(FileSizeExceededException.class,
                () -> fileValidator.validateFileSize(file, maxAllowedSize));
    }

    @Test
    public void testValidateFileIsImage() {
        // act and assert
        assertDoesNotThrow(() -> fileValidator.validateFileIsImage(file));
    }

    @Test
    public void testValidateFileIsImageFails() {
        // arrange
        MockMultipartFile txtFile = new MockMultipartFile(
                "file",
                "file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[5000]
        );

        // act and assert
        assertThrows(DataValidationException.class,
                () -> fileValidator.validateFileIsImage(txtFile));
    }
}
