package faang.school.projectservice.service.amazonclient;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonClientService {

    private final AmazonS3 s3client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private final ImageService imageService;

    public String updateProjectCover(MultipartFile multipartFile) {
        String fileName = new Date().getTime() + "-" +
                multipartFile.getOriginalFilename().replace(" ", "_");
        try {
            File file = convertMultiPartToFile(multipartFile);
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (IOException e) {
            log.warn("IOException: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
        return fileName;
    }

    public byte[] getProjectCover(String fileName) {

        try (InputStream inputStream = downloadFileTos3bucket(fileName)){
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.warn("IOException: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
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

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    private InputStream downloadFileTos3bucket(String fileName) {
        GetObjectRequest request = new GetObjectRequest(bucketName, fileName);
        return s3client.getObject(request).getObjectContent();
    }
}
