package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Service
@Validated
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        String type = file.getContentType();
        String originalName = file.getOriginalFilename();
        String key = String.format("%s/%d:%s", folder, System.currentTimeMillis(), originalName);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(type);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to upload file to S3: " + e.getMessage(), e);
        }

        return Resource.builder()
                .name(originalName)
                .key(key)
                .size(BigInteger.valueOf(fileSize))
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType(type))
                .build();
    }

    public void deleteResource(@NotBlank String key) {
        s3Client.deleteObject(bucketName, key);
    }
}
