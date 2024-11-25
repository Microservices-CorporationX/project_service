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
        String key = (String.format("%s/%d/%s", folder,
                System.currentTimeMillis(), file.getOriginalFilename()));
        log.info("Start uploading file with key: {}", key);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                    key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new FileUploadException("Error uploading file with key: " + key);
        }
        log.info("End uploading file with key: {}", key);
        return key;
    }

    public S3ObjectInputStream downloadFile(String key) {
        return downloadFileFromS3(key);
    }

    public Map<String, S3ObjectInputStream> downloadAllFiles(Map<String, String> filesNamesWithKeys) {
        Map<String, S3ObjectInputStream> result = new HashMap<>();
        filesNamesWithKeys.forEach((name, key) -> result.put(name, downloadFileFromS3(key)));
        return result;
    }
    private S3ObjectInputStream downloadFileFromS3(String key) {
        log.info("Start downloading file with key: {}", key);
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            S3ObjectInputStream fileStream = s3Object.getObjectContent();
            log.info("End downloading file with key: {}", key);
            return fileStream;
        } catch (AmazonS3Exception e) {
            throw new FileDownloadException("Error downloading file with key: " + key);
        }
    }

    public void deleteFile(String key) {
        log.info("Deleting file with key {}", key);
        s3Client.deleteObject(bucketName, key);
    }

//    private S3ObjectInputStream downloadFileFromS3(String key) {
//        log.info("Start downloading file with key: {}", key);
//        try {
//            S3Object s3Object = s3Client.getObject(bucketName, key);
//            S3ObjectInputStream fileStream = s3Object.getObjectContent();
//            log.info("End downloading file with key: {}", key);
//            return fileStream;
//        } catch (AmazonS3Exception e) {
//            throw new FileDownloadException("Error downloading file with key: " + key);
//        }
//    }
}
