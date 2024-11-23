package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
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

    public String uploadFile(byte[] bytes, ObjectMetadata metadata) {
        String key = generateHashKey(new ByteArrayInputStream(bytes));
        s3Client.putObject(bucketName, key, new ByteArrayInputStream(bytes), metadata);
        return key;
    }

    private String generateHashKey(InputStream inputStream) {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            byte[] hashBytes = digest.digest();
            return DatatypeConverter.printHexBinary(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }
}
