package faang.school.projectservice.service.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface S3Service {
    void toS3File(MultipartFile file, String bucketName, String key) throws IOException;

    void toS3File(String bucketName, String key, String contentType, InputStream inputStream);

    void deleteFile(String bucketName, String key);

    InputStream fromS3File(String bucketName, String key);

    String getKeyName();
}