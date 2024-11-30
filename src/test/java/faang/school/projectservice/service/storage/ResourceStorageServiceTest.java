package faang.school.projectservice.service.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.config.s3.AwsS3Config;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yaml")
@ActiveProfiles("test")
class ResourceStorageServiceTest {
    @MockBean
    private AwsS3Config awsS3Config;
    @Autowired
    private ResourceStorageService awsS3Service;
    @MockBean
    private AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Test
    void uploadResource() throws IOException {
        MultipartFile expectedResource = mock(MultipartFile.class);

        when(expectedResource.getSize()).thenReturn(1024L);
        when(expectedResource.getOriginalFilename()).thenReturn("test.png");
        when(expectedResource.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[1024]));

        Resource actualResource = awsS3Service.uploadResource("test-folder", expectedResource);

        assertNotNull(actualResource);
        assertEquals(expectedResource.getOriginalFilename(), actualResource.getName());
        assertEquals(ResourceStatus.ACTIVE, actualResource.getStatus());
    }

    @Test
    void downloadResource() {
        String key = "test-folder/20231126_test.png";
        S3Object s3ObjectMock = mock(S3Object.class);
        S3ObjectInputStream inputStreamMock = mock(S3ObjectInputStream.class);

        when(s3ObjectMock.getObjectContent()).thenReturn(inputStreamMock);
        when(amazonS3.getObject(bucketName, key)).thenReturn(s3ObjectMock);

        InputStream result = awsS3Service.downloadResource(key);

        verify(amazonS3, times(1)).getObject(bucketName, key);

        assertNotNull(result);
    }

    @Test
    void updateResource() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String key = "test-folder/20231126_test.png";

        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn(ResourceType.IMAGE.toString());
        when(file.getOriginalFilename()).thenReturn("updated.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[1024]));

        Resource resource = awsS3Service.updateResource(key, file);

        verify(amazonS3, times(1)).putObject(eq(bucketName), eq(key), any(InputStream.class), any(ObjectMetadata.class));

        assertNotNull(resource);
        assertEquals("updated.png", resource.getName());
        assertEquals(ResourceStatus.ACTIVE, resource.getStatus());
    }

    @Test
    void deleteResource() {
        String key = "test-folder/20231126_test.png";

        awsS3Service.deleteResource(key);

        verify(amazonS3, times(1)).deleteObject(bucketName, key);
    }
}