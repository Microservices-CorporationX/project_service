package faang.school.projectservice.service.amazonclient;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.exception.InvalidFormatFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmazonClientServiceTest {

    @Mock
    private AmazonS3 s3client;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private S3Object s3Object;

    @InjectMocks
    private AmazonClientService amazonClientService;

    @Test
    void updateProjectCoverInvalidFormatTest() {
        when(multipartFile.getOriginalFilename()).thenReturn("file.txt");

        assertThrows(InvalidFormatFile.class, () -> amazonClientService.updateProjectCover(multipartFile));
    }

    @Test
    void updateProjectCoverThrowsIOExceptionTest() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("first.png");
        when(multipartFile.getInputStream()).thenThrow(IOException.class);

        assertThrows(RuntimeException.class, () -> amazonClientService.updateProjectCover(multipartFile));
    }

    @Test
    void updateProjectCoverTest() throws IOException {

        String fileName = "image.png";
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());

        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        String result = amazonClientService.updateProjectCover(multipartFile);

        assertTrue(result.contains("-" + fileName));
    }

    @Test
    void getProjectCoverHandlesIOExceptionTest() throws IOException {

        S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);
        when(s3client.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenThrow(new IOException("Test exception"));

        assertThrows(RuntimeException.class, () -> amazonClientService.getProjectCover("test.png"));
    }

    @Test
    void getProjectCoverTest() {
        String fileName = "first.png";
        InputStream inputStream = new ByteArrayInputStream("test image".getBytes());
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(inputStream);

        when(s3client.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);

        byte[] result = amazonClientService.getProjectCover(fileName);

        assertEquals("test image", new String(result));
    }
}