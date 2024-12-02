package faang.school.projectservice.service.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceStorageService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final AmazonS3 amazonS3;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadResource(String folder, MultipartFile file) {
        ObjectMetadata metadata = createMetadata(file.getSize(), file.getContentType());
        String key = String.format("%s/%s_%s",
                folder,
                LocalDateTime.now().format(DATE_FORMATTER),
                file.getOriginalFilename());

        try{
            amazonS3.putObject(bucketName, key, file.getInputStream(), metadata);
        } catch (Exception e) {
            log.error("Error uploading file", e);
            throw new IllegalStateException("Error uploading file", e);
        }

        return createResource(file, key);
    }

    public InputStream downloadResource(String key){
        try{
            S3Object resource = amazonS3.getObject(bucketName, key);
            return resource.getObjectContent();
        } catch (Exception e){
            log.error("Resource downloading error", e);
            throw new IllegalStateException("Resource downloading error");
        }
    }

    public Resource updateResource(String key, MultipartFile file) {
        ObjectMetadata metadata = createMetadata(file.getSize(), file.getContentType());

        try {
            amazonS3.putObject(bucketName, key, file.getInputStream(), metadata);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return createResource(file, key);
    }

    public void deleteResource(String key){
        try{
            amazonS3.deleteObject(bucketName, key);
            log.info("Resource with key: {} was successfully deleted from bucket: {}", key, bucketName);
        } catch (Exception e){
            log.error("Resource deleting error", e);
            throw new IllegalStateException("Resource deleting error");
        }
    }

    @NotNull
    private Resource createResource(MultipartFile file, String key) {
        Resource createdResource = new Resource();
        createdResource.setKey(key);
        createdResource.setSize(BigInteger.valueOf(file.getSize()));
        createdResource.setName(file.getOriginalFilename());
        createdResource.setStatus(ResourceStatus.ACTIVE);
        createdResource.setType(ResourceType.getResourceType(file.getContentType()));

        return createdResource;
    }

    private ObjectMetadata createMetadata(long size, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(size);
        metadata.setContentType(contentType);
        return metadata;
    }
}
