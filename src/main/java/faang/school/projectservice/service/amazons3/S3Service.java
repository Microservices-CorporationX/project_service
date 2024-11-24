package faang.school.projectservice.service.amazons3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.exception.FileDownloadException;
import faang.school.projectservice.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folder) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        String key = (String.format("%s/%d%s", folder,
                System.currentTimeMillis(), file.getOriginalFilename()));

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                    key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new FileUploadException("Error uploading file");
        }
        return key;
    }

    public S3ObjectInputStream downloadFile(String key) {
        return downloadFileFromS3(key);
    }

    public Map<String, S3ObjectInputStream> downloadAllFiles(Map<String, String> fileNamesWithKeys) {
        Map<String, S3ObjectInputStream> result = new HashMap<>();
        fileNamesWithKeys.forEach((name, key) -> result.put(name, downloadFileFromS3(key)));
        return result;
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    private S3ObjectInputStream downloadFileFromS3(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (AmazonS3Exception e) {
            throw new FileDownloadException("Error downloading file");
        }
    }
}
