package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "service.s3", havingValue = "true")
public class S3Util {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public void s3UploadFile(MultipartFile file, String fileKey) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, fileKey, file.getInputStream(), objectMetadata
            );
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error("File not found", e);
            throw new FileException("File not found");
        } catch (Exception e) {
            log.error("Error uploading file to S3", e);
            throw new FileException("Error uploading file to S3");
        }
        log.info("File uploaded to S3: {}", fileKey);
    }

    public void s3DeleteFile(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
        } catch (Exception e) {
            log.error("Error deleting file from S3", e);
            throw new FileException("Error deleting file from S3");
        }
    }
}
