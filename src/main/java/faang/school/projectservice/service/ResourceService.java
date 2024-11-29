package faang.school.projectservice.service;

import faang.school.projectservice.handler.ResourceHandler;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.ResourceValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceHandler resourceHandler;
    private final ResourceValidator resourceValidator;
    private final ProjectValidator projectValidator;
    private final StorageService storageService;
    private final ProjectService projectService;

    @Transactional
    public String uploadProjectCover(MultipartFile file, long userId, long projectId) {
        projectValidator.validateProjectExistsById(projectId);
        projectValidator.validateUserIsProjectOwner(userId, projectId);
        resourceValidator.validateResourceNotEmpty(file);
        resourceValidator.validateProjectCoverSize(file);

        BufferedImage coverImage = resourceHandler.getImageFromMultipartFile(file);
        if (!resourceValidator.isCorrectProjectCoverScale(coverImage)) {
            coverImage = resourceHandler.resizeImage(coverImage,
                    ResourceValidator.MAX_COVER_WIDTH_PX,
                    ResourceValidator.MAX_COVER_HEIGHT_PX);
        }

        MultipartFile coverFile = resourceHandler.convertImageToMultipartFile(file, coverImage);

        Project project = projectService.getProjectById(projectId);

        if (projectValidator.hasProjectCoverImage(project)) {
            deleteProjectCover(userId, projectId);
        }

        String folderName = String.format("project_covers/%d", projectId);
        String key = String.format("%s/%d_%s", folderName, System.currentTimeMillis(), file.getOriginalFilename());
        storageService.uploadResource(coverFile, key);

        project.setCoverImageId(key);
        projectService.saveProject(project);

        log.info("Project #{} cover successfully uploaded.", projectId);
        return key;
    }

    @Transactional
    public void deleteProjectCover(long userId, long projectId) {
        projectValidator.validateProjectExistsById(projectId);
        projectValidator.validateUserIsProjectOwner(userId, projectId);

        Project project = projectService.getProjectById(projectId);

        if (!projectValidator.hasProjectCoverImage(project)) {
            throw new IllegalStateException(String.format("Project #%d has no cover image to delete.", projectId));
        }

        String key = project.getCoverImageId();
        project.setCoverImageId(null);
        projectService.saveProject(project);

        storageService.deleteResource(key);
        log.info("Project #{} cover successfully deleted.", projectId);
    }

    public byte[] downloadProjectCover(long userId, long projectId) {
        projectValidator.validateProjectExistsById(projectId);
        projectValidator.validateUserIsProjectOwner(userId, projectId);

        Project project = projectService.getProjectById(projectId);
        byte[] coverAsBytes = storageService.downloadResource(project.getCoverImageId());

        log.info("Project #{} cover successfully downloaded.", projectId);
        return coverAsBytes;
    }
}