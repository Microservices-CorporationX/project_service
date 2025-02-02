package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.is-mocked", havingValue = "false", matchIfMissing = true)
public class S3ServiseImpl implements S3Service {
    private final AmazonS3 s3Client;
    @Value("${services.s3.bucketname}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        String fileName = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            s3Client.putObject(new PutObjectRequest(bucketName, fileName,
                    file.getInputStream(), metadata));
            log.info("Object with name{} success downloaded", fileName);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException("Error while downloading file.");
        }
        return fileName;
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
        log.info("Object with key{} success deleted", key);
    }

    @Override
    public InputStream downloadFile(String key) {
        S3Object s3Object = s3Client.getObject(bucketName, key);
        return s3Object.getObjectContent();
    }
}
