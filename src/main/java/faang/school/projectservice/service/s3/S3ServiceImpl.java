package faang.school.projectservice.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());

        String sanitizedFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String key = String.format("%s/xd%s-%s", folder, System.currentTimeMillis(), sanitizedFileName);

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    key,
                    inputStream,
                    objectMetadata
            );
            log.info("S3ServiceImpl upload file.getContentType(): {}, {}", file.getContentType(), objectMetadata.getContentType());
            s3Client.putObject(putObjectRequest);
        } catch (IOException | AmazonServiceException e) {
            log.error("Failed to upload file to S3: {}", e.getMessage());
            throw new FileException(ErrorMessage.FILE_EXCEPTION, e);
        }

        return Resource.builder()
                .key(key)
                .size(BigInteger.valueOf(fileSize))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType(file.getContentType()))
                .name(file.getOriginalFilename())
                .build();
    }

//    @Override
//    public Resource uploadFile(MultipartFile file, String folder) {
//        long fileSize = file.getSize();
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentLength(fileSize);
//        objectMetadata.setContentType(file.getContentType());
//        String key = String.format("%s/xd%s%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
//
//        try {
//            PutObjectRequest putObjectRequest = new PutObjectRequest(
//                    bucketName,
//                    key,
//                    file.getInputStream(),
//                    objectMetadata
//            );
//            s3Client.putObject(putObjectRequest);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            throw new FileException(ErrorMessage.FILE_EXCEPTION);
//        }
//
//        Resource resource = new Resource();
//        resource.setKey(key);
//        resource.setSize(BigInteger.valueOf(fileSize));
//        resource.setCreatedAt(LocalDateTime.now());
//        resource.setUpdatedAt(LocalDateTime.now());
//        resource.setStatus(ResourceStatus.ACTIVE);
//        resource.setType(ResourceType.getResourceType(file.getContentType()));
//        resource.setName(file.getOriginalFilename());
//
//        return resource;
//    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileException(ErrorMessage.FILE_EXCEPTION);
        }
    }
}
