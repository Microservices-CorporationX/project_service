package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @InjectMocks
    private S3Service s3Service;

    @Mock
    private AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucket;

    @Test
    public void testUploadFile() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[5000]
        );
        InputStream inputStream = file.getInputStream();
        String folder = "/projects";

        // act
        s3Service.uploadFile(file, inputStream, folder);

        // assert
        verify(amazonS3).putObject(
                eq(bucket),
                anyString(),
                eq(inputStream),
                any(ObjectMetadata.class)
        );
    }

    @Test
    public void testDeleteFile() {
        // arrange
        String key = "someKey";

        // act
        s3Service.deleteFile(key);

        // assert
        verify(amazonS3).deleteObject(any(DeleteObjectRequest.class));
    }

}
