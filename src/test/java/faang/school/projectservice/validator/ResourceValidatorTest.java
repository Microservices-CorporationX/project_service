package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EmptyResourceException;
import faang.school.projectservice.exception.InsufficientStorageException;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceValidatorTest {

    @InjectMocks
    ResourceValidator resourceValidator;

    MockMultipartFile file;

    @Test
    @DisplayName("Validate Resource is not empty: success")
    void validateResourceNotEmpty_FileWithContent_Success() {
        file = new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "content".getBytes());

        assertDoesNotThrow(() -> resourceValidator.validateResourceNotEmpty(file));
    }

    @Test
    @DisplayName("Validate Resource is empty: fail")
    void validateResourceNotEmpty_FileWithoutContent_Fail() {
        file = new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "".getBytes());

        Exception ex = assertThrows(EmptyResourceException.class, () -> resourceValidator.validateResourceNotEmpty(file));
        assertEquals(String.format("File %s is empty. It cannot be uploaded", file.getName()), ex.getMessage());
    }

    @Test
    @DisplayName("Validate enough space in storage: success")
    void validateEnoughSpaceInStorage_EnoughSpace_Success() {
        file = new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "content".getBytes());

        Project project = Project.builder().
                storageSize(BigInteger.valueOf(10)).
                maxStorageSize(BigInteger.valueOf(100)).
                build();

        assertDoesNotThrow(() -> resourceValidator.validateEnoughSpaceInStorage(project, file));
    }

    @Test
    @DisplayName("Validate space in storage: available space is not enough: success")
    void validateEnoughSpaceInStorage_NotEnoughAvailableSpace_Fail() {
        file = new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "content".getBytes());

        Project project = Project.builder().
                storageSize(BigInteger.valueOf(8)).
                maxStorageSize(BigInteger.valueOf(10)).
                build();

        Exception ex = assertThrows(InsufficientStorageException.class, () -> resourceValidator.validateEnoughSpaceInStorage(project, file));
        assertEquals("Not enough space to store files", ex.getMessage());
    }


}