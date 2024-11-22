package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            InputStream inputStream = file.getInputStream();
            try (inputStream) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                MessageDigest digest = MessageDigest.getInstance("SHA-256");

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }

                byte[] hashBytes = digest.digest();
                String key = DatatypeConverter.printHexBinary(hashBytes);

                s3Client.putObject(bucketName, key, file.getInputStream(), metadata);
                return key;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported");
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file");
        }
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }
}
