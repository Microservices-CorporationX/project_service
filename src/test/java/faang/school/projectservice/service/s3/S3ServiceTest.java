package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @InjectMocks
    private S3Service s3Service;

    @Mock
    private AmazonS3 amazonS3;

    private String bucket = "testBucket";

    private MultipartFile multipartFile;

    private String key = "key";

    @BeforeEach
    void setUp() {
        multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes());
        ReflectionTestUtils.setField(s3Service, "bucketName", bucket);
    }

    @Test
    void testAddResource() {
        Resource resource = s3Service.addResource(multipartFile, key);

        verify(amazonS3).putObject(any(PutObjectRequest.class));

        assertNotNull(resource);
        assertEquals(key, resource.getKey());
        assertEquals(multipartFile.getOriginalFilename(), resource.getName());
        assertEquals(BigInteger.valueOf(multipartFile.getSize()), resource.getSize());
        assertEquals(ResourceStatus.ACTIVE, resource.getStatus());
        assertNotNull(resource.getCreatedAt());
        assertNotNull(resource.getUpdatedAt());
    }
    @Test
    void testUpdateResource(){
        s3Service.updateResource(multipartFile,key);

        verify(amazonS3).deleteObject(bucket,key);
        verify(amazonS3).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testCompleteRemoval(){
        s3Service.completeRemoval(key);

        verify(amazonS3).deleteObject(bucket, key);
    }
    @Test
    void testGeneratePresignedUrl(){
        String expectedUrl = "http://key";
        URL url;
        try {
            url = new URL(expectedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(url);

        String result = s3Service.generatePresignedUrl(key);

        assertEquals(result,expectedUrl);
    }
}