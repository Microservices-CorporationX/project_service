package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiseImpl implements S3Service {
    private final AmazonS3 s3client;
    @Value("${services.s3.bucketName}")
    private String bucketname;

    @Override
    public void uploadFile(MultipartFile file, String folder) {

    }

    @Override
    public void deleteFile(String key) {

    }

    @Override
    public InputStream downloadFile(String key) {
        return null;
    }
}
