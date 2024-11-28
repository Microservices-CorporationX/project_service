package faang.school.projectservice.service.project;

import faang.school.projectservice.exception.project.StorageSizeExceededException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.MultipartImage.MultipartImage;
import faang.school.projectservice.service.project.s3.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private static final List<Integer> IMAGE_DIMENSIONS = List.of(1080, 566);

    public Optional<Project> findProject(Long id){
        if (id == null) {
            throw new IllegalArgumentException("Project not found");
        }

        return Optional.ofNullable(projectRepository.getProjectById(id));
    }

    @Transactional
    public String uploadCoverImage(long projectId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSize(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        String key = s3Service.uploadCoverImage(file, folder);
        project.setStorageSize(newStorageSize);
        project.setCoverImageId(key);
        projectRepository.save(project);
        return key;
    }

    public MultipartFile validateImageResolution(MultipartFile file) throws IOException {
        if (file == null) {
            log.error("Received a request to validate image resolution with null file");
            throw new IllegalArgumentException("There is no file provided");
        }

        BufferedImage image = ImageIO.read(file.getInputStream());
        int width = image.getWidth();
        int height = image.getHeight();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int targetWidth = IMAGE_DIMENSIONS.get(0);
        int targetHeight = IMAGE_DIMENSIONS.get(1);
        int secondTargetHeight = IMAGE_DIMENSIONS.get(0);

        if (width > targetWidth && height > targetHeight && width > height) {
            resizeImage(file, targetWidth, targetHeight, byteArrayOutputStream);
        }

        if (width > targetWidth && height > secondTargetHeight && width == height) {
            resizeImage(file, targetWidth, secondTargetHeight, byteArrayOutputStream);
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        return new MultipartImage(
                file.getBytes(),
                file.getName(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                byteArrayInputStream);
    }

    private void checkStorageSize(BigInteger newStorageSize, BigInteger maxStorageSize) {
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            log.error("Received a request to upload an image that exceeds the total storage");
            throw new StorageSizeExceededException("Storage size exceeded! Choose a smaller image.");
        }
    }

    private void resizeImage(MultipartFile file,
                                     int targetWidth,
                                     int targetHeight,
                                     ByteArrayOutputStream outputStream) throws IOException {
        Thumbnails.of(file.getInputStream())
                .size(targetWidth, targetHeight)
                .keepAspectRatio(true)
                .toOutputStream(outputStream);
    }
}
