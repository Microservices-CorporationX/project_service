package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.FileWriteReadS3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.is-mocked", havingValue = "false", matchIfMissing = true)
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;

    @Override
    public void toS3File(String bucketName, String key, String contentType, InputStream inputStream) {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);

        toS3File(bucketName, key, contentType, inputStream, objectMetadata);
    }

    @Override
    public void toS3File(String bucketName, String key, MultipartFile file) throws IOException {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());

        toS3File(bucketName, key, file.getContentType(), file.getInputStream(), objectMetadata);
    }

    private void toS3File(String bucketName, String key, String contentType, InputStream inputStream, ObjectMetadata objectMetadata) {
        log.info("Start save / update a file to S3 for: bucketName:{}, key:{}, contentType:{}, inputStream ...",
                bucketName, key, contentType);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    key,
                    inputStream,
                    objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileWriteReadS3Exception(e.getMessage());
        }
    }

    @Override
    public InputStream fromS3File(String bucketName, String key) {
        log.info("Start upload a file from S3 for: bucketName:{}, key:{}",
                bucketName, key);
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileWriteReadS3Exception(e.getMessage());
        }
    }

    @Override
    public void deleteFile(String bucketName, String key) {
        log.info("Start delete a file in S3 for: bucketName:{}, key:{}",
                bucketName, key);
        s3Client.deleteObject(bucketName, key);
    }

    @Override
    public String getKeyName() {
        return UUID.randomUUID().toString();
    }
}