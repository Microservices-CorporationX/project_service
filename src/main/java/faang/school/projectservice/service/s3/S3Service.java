package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Setter
public class S3Service {
    private final AmazonS3 s3Client;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception ex) {
            log.error("Exception when saving file to s3", ex);
            throw new IllegalStateException("Failed to upload the file");
        }
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setName(file.getOriginalFilename());
        return resource;
    }

    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("Could not download the file with key {}", key);
            throw new IllegalStateException("File could no be downloaded");
        }
    }
}
