package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private AmazonS3 s3Client;
    @Value("${services.s3.bucketName}")
    private String bucketName;
    @InjectMocks
    private S3Service s3Service;
    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectRequestArgumentCaptor;

    @BeforeEach
    public void setUp() {
        s3Service.setBucketName(bucketName);
    }

    @Test
    void testUploadFileUploaded() throws IOException {
        String fileContent = "content";
        String fileName = "file name";
        String originalFileName = "original";
        String contentType = "image";
        String folder = "folder";
        MultipartFile file = new MockMultipartFile(fileName, originalFileName,
                contentType, fileContent.getBytes(StandardCharsets.UTF_8));
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        Resource result = s3Service.uploadFile(file, folder);

        verify(s3Client).putObject(putObjectRequestArgumentCaptor.capture());
        PutObjectRequest capturedPutObjectRequest = putObjectRequestArgumentCaptor.getValue();
        assertEquals(objectMetadata.getRawMetadata(), capturedPutObjectRequest.getMetadata().getRawMetadata());
        assertEquals(bucketName, capturedPutObjectRequest.getBucketName());
        assertArrayEquals(fileContent.getBytes(), capturedPutObjectRequest.getInputStream().readAllBytes());
        assertTrue(capturedPutObjectRequest.getKey().startsWith(folder + "/"));
        assertTrue(capturedPutObjectRequest.getKey().contains(file.getOriginalFilename()));
        assertEquals(capturedPutObjectRequest.getKey(), result.getKey());
        assertEquals(BigInteger.valueOf(file.getSize()), result.getSize());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertEquals(ResourceStatus.ACTIVE, result.getStatus());
        assertEquals(ResourceType.getResourceType(file.getContentType()), result.getType());
        assertEquals(file.getOriginalFilename(), result.getName());
    }

    @Test
    void testUploadFileWithException() {
        String fileContent = "content";
        String fileName = "file name";
        String originalFileName = "original";
        String contentType = "image";
        String folder = "folder";
        MultipartFile file = new MockMultipartFile(fileName, originalFileName,
                contentType, fileContent.getBytes(StandardCharsets.UTF_8));
        when(s3Client.putObject(any())).thenThrow(new RuntimeException());

        IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> s3Service.uploadFile(file, folder));

        assertEquals("Failed to upload the file", exception.getMessage());
    }

    @Test
    void testDownloadFileDownloaded() throws IOException {
        S3Object s3Object = new S3Object();
        String data = "some data";
        InputStream inputStream = IOUtils.toInputStream(data, "utf-8");
        s3Object.setObjectContent(inputStream);
        String key = "key";
        when(s3Client.getObject(bucketName, key)).thenReturn(s3Object);
        InputStream result = s3Service.downloadFile(key);
        verify(s3Client).getObject(bucketName, key);
        assertArrayEquals(data.getBytes(), result.readAllBytes());
    }

    @Test
    void testDownloadFileWithException() {
        when(s3Client.getObject(any(String.class), any(String.class))).thenThrow(new RuntimeException());
        IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> s3Service.downloadFile("key"));
        assertEquals("File could no be downloaded", exception.getMessage());
    }
}