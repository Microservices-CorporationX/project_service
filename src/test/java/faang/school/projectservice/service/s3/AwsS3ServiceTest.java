package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.config.s3.AwsS3Client;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyString;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yaml")
@ActiveProfiles("test")
class AwsS3ServiceTest {
    @MockBean
    private AwsS3Client awsS3Client;
    @Autowired
    private AwsS3Service awsS3Service;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Test
    void uploadResource() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        AmazonS3 s3Mock = mock(AmazonS3.class);

        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("test.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[1024]));
        when(awsS3Client.s3Config()).thenReturn(s3Mock);

        Resource resource = awsS3Service.uploadResource("test-folder", file);

        verify(s3Mock).putObject(eq(bucketName), anyString(), any(InputStream.class), any(ObjectMetadata.class));

        assertNotNull(resource);
        assertEquals("test.png", resource.getName());
        assertEquals(ResourceStatus.ACTIVE, resource.getStatus());
    }

    @Test
    void downloadResource() {
        String key = "test-folder/20231126_test.png";
        S3Object s3ObjectMock = mock(S3Object.class);
        S3ObjectInputStream inputStreamMock = mock(S3ObjectInputStream.class);
        AmazonS3 s3Mock = mock(AmazonS3.class);

        when(s3ObjectMock.getObjectContent()).thenReturn(inputStreamMock);
        when(awsS3Client.s3Config()).thenReturn(s3Mock);
        when(s3Mock.getObject(bucketName, key)).thenReturn(s3ObjectMock);

        InputStream result = awsS3Service.downloadResource(key);

        verify(s3Mock).getObject(bucketName, key);

        assertNotNull(result);
    }

    @Test
    void updateResource() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        AmazonS3 s3Mock = mock(AmazonS3.class);
        String key = "test-folder/20231126_test.png";

        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("updated.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[1024]));
        when(awsS3Client.s3Config()).thenReturn(s3Mock);

        Resource resource = awsS3Service.updateResource(key, file);

        verify(s3Mock).putObject(eq(bucketName), eq(key), any(InputStream.class), any(ObjectMetadata.class));

        assertNotNull(resource);
        assertEquals("updated.png", resource.getName());
        assertEquals(ResourceStatus.ACTIVE, resource.getStatus());
    }

    @Test
    void deleteResource() {
        String key = "test-folder/20231126_test.png";
        AmazonS3 s3Mock = mock(AmazonS3.class);

        when(awsS3Client.s3Config()).thenReturn(s3Mock);

        awsS3Service.deleteResource(key);

        verify(s3Mock).deleteObject(bucketName, key);
    }
}