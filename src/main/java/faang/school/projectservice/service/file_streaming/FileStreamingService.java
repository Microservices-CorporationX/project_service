package faang.school.projectservice.service.file_streaming;

import faang.school.projectservice.exception.StreamingFileError;
import faang.school.projectservice.exception.ZippingFileError;
import lombok.extern.slf4j.Slf4j;
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

    public StreamingResponseBody getStreamingResponseBody(InputStream fileStream) {
        if (fileStream == null) {
            throw new IllegalArgumentException("Stream can't be empty");
        }

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

    public StreamingResponseBody getStreamingResponseBodyInZip(Map<String, InputStream> files) {
        if (files == null) {
            throw new IllegalArgumentException("Files streams can't be empty");
        }
        return outputStream -> {
            try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
                for (Map.Entry<String, InputStream> entry : files.entrySet()) {
                    String fileName = entry.getKey();
                    InputStream fileStream = entry.getValue();

                    try (fileStream) {
                        byte[] buffer = new byte[1024];
                        int bytesRead = fileStream.read(buffer);
                        if (bytesRead == -1) {
                            log.error("File {} is empty. Skipping...", fileName);
                            continue;
                        }
                        zipOut.putNextEntry(new ZipEntry(fileName));

                        do {
                            zipOut.write(buffer, 0, bytesRead);
                        } while ((bytesRead = fileStream.read(buffer)) != -1);
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
