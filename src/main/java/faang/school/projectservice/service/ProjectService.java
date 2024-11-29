package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.utils.image.ImageUtils;
import faang.school.projectservice.validator.FileValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final FileValidator fileValidator;
    private final S3Service s3Service;
    private final ImageUtils imageUtils;

    @Transactional
    public void addCover(long projectId, MultipartFile file) {
        log.info("Trying to add cover to project: {}", projectId);

        long maxAllowedSize = 5 * 1024 * 1024;
        fileValidator.validateFileSize(file, maxAllowedSize);
        fileValidator.validateFileIsImage(file);

        int maxWidth = 1080;
        int maxHeight = 566;
        BufferedImage image = imageUtils.getResizedBufferedImage(file, maxWidth, maxHeight);
        InputStream inputStream = imageUtils.getBufferedImageInputStream(file, image);

        String folder = "projectCovers";
        String coverImageId = s3Service.uploadFile(file, inputStream, folder);

        Project project = findProjectById(projectId);
        String oldCoverImageId = project.getCoverImageId();
        if (oldCoverImageId != null) {
            s3Service.deleteFile(oldCoverImageId);
        }

        project.setCoverImageId(coverImageId);
    }

    public Project findProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }
}
