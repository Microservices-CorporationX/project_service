package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.FileWriteReadS3Exception;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    @Mock
    AmazonS3 s3Client;

    @InjectMocks
    S3ServiceImpl s3ServiceImpl;

    String bucketName = "test-bucket";
    String key = "123456-1234-1234-12345678";

    @Test
    void testToS3FileSuccess() {
        String contentType = "jpg";
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);

        s3ServiceImpl.toS3File(bucketName, key, contentType, inputStream);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testToS3FileShouldThrowExceptionFail() {
        String contentType = "jpg";
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        String message = "Message: Saved fail.";

        doThrow(new RuntimeException(message)).when(s3Client).putObject(any(PutObjectRequest.class));

        FileWriteReadS3Exception exception = assertThrows(FileWriteReadS3Exception.class, () ->
                s3ServiceImpl.toS3File(bucketName, key, contentType, inputStream));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testFromS3FileSuccess() {
        S3Object s3Object = mock(S3Object.class);
        when(s3Client.getObject(anyString(), anyString())).thenReturn(s3Object);

        s3ServiceImpl.fromS3File(bucketName, key);
        verify(s3Client, times(1)).getObject(bucketName, key);
    }

    @Test
    void testFromS3FileShouldThrowExceptionFail() {
        String message = "Message: Upload fail.";
        when(s3Client.getObject(anyString(), anyString())).thenThrow(new RuntimeException(message));
        FileWriteReadS3Exception exception = assertThrows(FileWriteReadS3Exception.class, () ->
                s3ServiceImpl.fromS3File(bucketName, key));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void deleteFile() {
        s3ServiceImpl.deleteFile(bucketName, key);
        verify(s3Client, times(1)).deleteObject(bucketName, key);
    }

    @Test
    void getKeyName() {
        String key = s3ServiceImpl.getKeyName();

        assertNotNull(key);
        assertTrue(key.matches("^[a-f\\d-]{36}$"));
    }
}