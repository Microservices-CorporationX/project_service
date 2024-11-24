package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.exceptions.ImageProcessingException;
import faang.school.projectservice.utils.image.ImageUtils;
import faang.school.projectservice.utils.image.ResizeOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;
    private final ImageUtils imageUtils;

    @Value("${services.s3.bucketName}")
    private String bucket;

    public String uploadFile(MultipartFile file, String folder, ResizeOptions resizeOptions) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());

        if (resizeOptions.requiresResizing()) {
            BufferedImage image = resizeImage(file, resizeOptions);
            InputStream inputStream = imageUtils.getBufferedImageInputStream(file, image);
            s3Client.putObject(bucket, key, inputStream, objectMetadata);
            return key;
        }

        try {
            s3Client.putObject(bucket, key, file.getInputStream(), objectMetadata);
        } catch (IOException ex) {
            throw new ImageProcessingException("An error occurred when converting MultipartFile to InputStream");
        }
        return key;
    }

    private BufferedImage resizeImage(MultipartFile file, ResizeOptions resizeOptions) {
        return imageUtils.getResizedBufferedImage(
                file,
                resizeOptions.maxWidth(),
                resizeOptions.maxHeight()
        );
    }
}
