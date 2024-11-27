package faang.school.projectservice.service.file_streaming;

import faang.school.projectservice.exception.StreamingFileError;
import faang.school.projectservice.exception.ZippingFileError;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@Validated
public class FileStreamingService {

    public StreamingResponseBody getStreamingResponseBody(
            @NotNull(message = "Stream can't be empty") InputStream fileStream) {

        return outputStream -> {
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
    }

    public StreamingResponseBody getStreamingResponseBodyInZip(
            @NotNull(message = "Stream can't be empty") Map<String, InputStream> files) {

        return outputStream -> {
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
    }
}
