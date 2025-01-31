package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.ErrorMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.processing.FilerException;
import java.io.*;
import java.time.LocalDateTime;

import static java.text.NumberFormat.Field.SUFFIX;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.is-mocked", havingValue = "false", matchIfMissing = true)
public class S3ServiseImpl implements S3Service {
    private final AmazonS3 s3client;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public void uploadFile(MultipartFile file, String folder) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);

        // create empty content
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        // create a PutObjectRequest passing the folder name suffixed by /
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                folder + SUFFIX, emptyContent, metadata);
        // send request to S3 to create folder
        s3client.putObject(putObjectRequest);
        String fileName = folder + SUFFIX + file.getName();
        try {
            s3client.putObject(new PutObjectRequest(bucketName, fileName,
                    file.getInputStream(), metadata));
        } catch (IOException e) {
            log.error(e.getMessage());
//            throw new ("dd");
        }
    }

    @Override
    public void deleteFile(String key) {

    }

    @Override
    public InputStream downloadFile(String key) {
        return null;
    }
}
