package faang.school.projectservice.service;

import faang.school.projectservice.config.s3.MinioConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private MinioConfigProperties minioConfigProperties;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private StorageService storageService;

    private String bucketName;
    private String key;
    private String contentType;
    private long size;
    private byte[] content;

    @BeforeEach
    void setUp() {
        bucketName = "test-bucket";
        key = "test-key";
        contentType = "image/jpeg";
        size = 3L;
        content = new byte[]{1, 2, 3};

        when(minioConfigProperties.getBucketName()).thenReturn(bucketName);
    }

    @Test
    void testUploadResource_Success() throws IOException {
        when(mockFile.getContentType()).thenReturn(contentType);
        when(mockFile.getSize()).thenReturn(size);
        when(mockFile.getBytes()).thenReturn(content);

        storageService.uploadResource(mockFile, key);

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadResource_IOException() throws IOException {
        when(mockFile.getBytes()).thenThrow(new IOException("Test IOException"));

        assertThatThrownBy(() -> storageService.uploadResource(mockFile, key))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        String.format("An error occurred while uploading file '%s'.", mockFile.getOriginalFilename())
                );
    }

    @Test
    void testDownloadResource_Success() {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        ResponseBytes<GetObjectResponse> responseBytes = ResponseBytes.fromByteArray(
                GetObjectResponse.builder().build(),
                content);
        when(s3Client.getObjectAsBytes(getObjectRequest)).thenReturn(responseBytes);

        byte[] result =  storageService.downloadResource(key);

        assertThat(result).isEqualTo(content);
    }

    @Test
    void testDeleteResource_Success() {
        storageService.deleteResource(key);

        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }
}