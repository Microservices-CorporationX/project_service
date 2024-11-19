package faang.school.projectservice.service.amazonclient;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.InvalidFormatFile;
import faang.school.projectservice.service.image.ImageService;
import jakarta.annotation.PostConstruct;
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

    private AmazonS3 s3client;

    @Value("${services.s3.endpoint}")
    private String endpointUrl;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;

    private final ImageService imageService;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpointUrl, "us-east-1"))
                .build();
    }

    public String updateProjectCover(MultipartFile multipartFile) {
        String fileName = new Date().getTime() + "-" +
                multipartFile.getOriginalFilename().replace(" ", "_");
        try {
            File file = convertMultiPartToFile(multipartFile);
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (IOException e) {
            log.error("IOException: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
        return fileName;
    }

    public byte[] getProjectCover(String fileName) {

        InputStream inputStream = downloadFileTos3bucket(fileName);
        try {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("IOException: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        String format = file.getOriginalFilename().split("\\.")[1];
        if (!(format.equalsIgnoreCase("png") ||
                format.equalsIgnoreCase("jpg") ||
                format.equalsIgnoreCase("jpeg"))) {
            throw new InvalidFormatFile(format + " not supported format");
        }

        BufferedImage outputImage = imageService.resizeImage(ImageIO.read(file.getInputStream()));

        File outputFile = new File(file.getOriginalFilename());
        ImageIO.write(outputImage, format, outputFile);

        return outputFile;
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
