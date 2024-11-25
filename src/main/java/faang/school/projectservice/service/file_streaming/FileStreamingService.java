package faang.school.projectservice.service.file_streaming;

import faang.school.projectservice.exception.StreamingFileError;
import faang.school.projectservice.exception.ZippingFileError;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class FileStreamingService {

    @NotNull(message = "Stream can't be empty")
    public ResponseEntity<StreamingResponseBody> getStreamingResponseBodyInResponseEntity(InputStream fileStream) {
        StreamingResponseBody responseBody = outputStream -> {
            log.info("Start transform InputStream to StreamResponseBody");
            try (fileStream) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new StreamingFileError("Error streaming file");
            }
        };
        log.info("Transform InputStream to StreamResponseBody successfully");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(responseBody);
    }

    @NotNull(message = "Stream can't be empty")
    public ResponseEntity<StreamingResponseBody> getStreamingResponseBodyInResponseEntityZip(
            Map<String, InputStream> files, long projectId) {
        StreamingResponseBody responseBody = outputStream -> {
            log.info("Start transform InputStream to StreamResponseBody in Zip format");
            try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
                for (Map.Entry<String, InputStream> entry : files.entrySet()) {
                    String fileName = entry.getKey();
                    InputStream fileStream = entry.getValue();

                    try (fileStream) {
                        zipOut.putNextEntry(new ZipEntry(fileName));

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fileStream.read(buffer)) != -1) {
                            zipOut.write(buffer, 0, bytesRead);
                        }
                        zipOut.closeEntry();
                    } catch (IOException e) {
                        log.error("Error processing file: {}. Skipping...", fileName);
                    }
                }
            } catch (IOException e) {
                throw new ZippingFileError("Error while zipping files");
            }
        };
        log.info("Transform InputStream to StreamResponseBody in Zip format successfully");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=project_" +
                        projectId + "_resources.zip")
                .body(responseBody);
    }
}
