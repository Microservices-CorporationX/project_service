package faang.school.projectservice.service.amazonclient;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.exception.FileDeleteException;
import faang.school.projectservice.exception.FileDownloadException;
import faang.school.projectservice.exception.FileUploadException;
import faang.school.projectservice.exception.InvalidFormatFile;
import faang.school.projectservice.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonClientService {

    private final AmazonS3 s3client;
    private final ImageService imageService;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String updateProjectCover(MultipartFile multipartFile) {
        String fileName = new Date().getTime() + "-" +
                multipartFile.getOriginalFilename().replace(" ", "_");
        try {
            File file = convertMultiPartToFile(multipartFile);
            uploadFileToS3Bucket(fileName, file);
            file.delete();
        } catch (IOException e) {
            log.warn("IOException: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
        return fileName;
    }

    public byte[] getProjectCover(String fileName) {

        try (InputStream inputStream = downloadFileFromS3Bucket(fileName)){
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.warn("IOException: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }

    public String uploadFile(MultipartFile file, String folder) {
        String key = (String.format("%s/%d%s", folder,
                System.currentTimeMillis(), file.getOriginalFilename()));
        log.info("Start uploading file with key: {}", key);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                    key, file.getInputStream(), objectMetadata);
            s3client.putObject(putObjectRequest);
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

    public void deleteFile(String key) {
        log.info("Deleting file with key {}", key);
        try {
            s3client.deleteObject(bucketName, key);
            log.info("Successfully deleted file with key {}", key);
        } catch (AmazonS3Exception e) {
            throw new FileDeleteException("Error deleting file with key: " + key);
        }
    }

    private S3ObjectInputStream downloadFileFromS3(String key) {
        log.info("Start downloading file with key: {}", key);
        try {
            S3Object s3Object = s3client.getObject(bucketName, key);
            S3ObjectInputStream fileStream = s3Object.getObjectContent();
            log.info("End downloading file with key: {}", key);
            return fileStream;
        } catch (AmazonS3Exception e) {
            throw new FileDownloadException("Error downloading file with key: " + key);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        String format = checkFormatFile(file);

        BufferedImage outputImage;
        try (InputStream inputStream = file.getInputStream()) {
            outputImage = imageService.resizeImage(ImageIO.read(inputStream));
        }

        File outputFile = new File(file.getOriginalFilename());
        ImageIO.write(outputImage, format, outputFile);

        return outputFile;
    }

    private String checkFormatFile(MultipartFile file) {
        String format = file.getOriginalFilename().split("\\.")[1];
        if (!(format.equalsIgnoreCase("png") ||
                format.equalsIgnoreCase("jpg") ||
                format.equalsIgnoreCase("jpeg"))) {
            throw new InvalidFormatFile(format + " not supported format");
        }
        return format;
    }

    private void uploadFileToS3Bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    private InputStream downloadFileFromS3Bucket(String fileName) {
        GetObjectRequest request = new GetObjectRequest(bucketName, fileName);
        return s3client.getObject(request).getObjectContent();
    }
}
