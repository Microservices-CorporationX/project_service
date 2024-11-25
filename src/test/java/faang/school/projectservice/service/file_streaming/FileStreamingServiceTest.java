package faang.school.projectservice.service.file_streaming;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class FileStreamingServiceTest {

    @InjectMocks
    private FileStreamingService fileStreamingService;

    @Test
    void getStreamingResponseBodyInResponseEntityTest() throws Exception {
        String content = "Sample content for testing";
        InputStream fileStream = new ByteArrayInputStream(content.getBytes());

        ResponseEntity<StreamingResponseBody> response =
                fileStreamingService.getStreamingResponseBodyInResponseEntity(fileStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamingResponseBody body = response.getBody();
        assertNotNull(body);
        body.writeTo(outputStream);

        assertEquals(content, outputStream.toString());
    }

    @Test
    void getStreamingResponseBodyInResponseEntityZipTest() throws Exception {
        long projectId = 123L;
        Map<String, InputStream> files = new HashMap<>();
        files.put("file1.txt", new ByteArrayInputStream("Content of file 1".getBytes()));
        files.put("file2.txt", new ByteArrayInputStream("Content of file 2".getBytes()));

        ResponseEntity<StreamingResponseBody> response =
                fileStreamingService.getStreamingResponseBodyInResponseEntityZip(files, projectId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamingResponseBody body = response.getBody();
        assertNotNull(body);
        body.writeTo(outputStream);

        try (ZipInputStream zipInputStream = new ZipInputStream(
                new ByteArrayInputStream(outputStream.toByteArray()))) {
            int fileCount = 0;
            while (zipInputStream.getNextEntry() != null) {
                fileCount++;
            }

            assertEquals(files.size(), fileCount);
        }
    }
}
