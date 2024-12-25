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
import software.amazon.awssdk.services.s3.model.S3Exception;

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
    public void uploadProjectCover(MultipartFile file, Long userId, Long projectId) {
        projectValidator.validateProjectExistsById(projectId);
        projectValidator.validateUserIsProjectOwner(userId, projectId);
        resourceValidator.validateResourceNotEmpty(file);
        resourceValidator.validateProjectCoverSize(file);

        MultipartFile coverFile = resourceHandler.handleImage(file);
        Project project = projectService.getProjectById(projectId);

        if (projectValidator.hasCoverImage(project)) {
            deleteProjectCover(userId, projectId);
        }

        String folderName = String.format("project_covers/%d", projectId);
        String key = String.format("%s/%d_%s", folderName, System.currentTimeMillis(), file.getOriginalFilename());

        try {
            storageService.uploadResource(coverFile, key);
        } catch (S3Exception ex) {
            log.error("An error occurred while uploading the cover for project with ID: {}. Error: {}",
                    projectId, ex.getMessage(), ex);
            throw new IllegalStateException(
                    String.format("Failed to upload cover image for project with ID %d", projectId), ex);
        }

        project.setCoverImageId(key);
        projectService.saveProject(project);

        log.info("Project {} cover successfully uploaded.", projectId);
    }

    @Transactional
    public void deleteProjectCover(Long userId, Long projectId) {
        projectValidator.validateProjectExistsById(projectId);
        projectValidator.validateUserIsProjectOwner(userId, projectId);

        Project project = projectService.getProjectById(projectId);

        if (!projectValidator.hasCoverImage(project)) {
            throw new IllegalStateException(String.format("Project %d has no cover image to delete.", projectId));
        }

        String key = project.getCoverImageId();

        try {
            storageService.deleteResource(key);
        } catch (S3Exception ex) {
            log.error("An error occurred while deleting the cover for project with ID: {}. Error: {}",
                    projectId, ex.getMessage(), ex);
            throw new IllegalStateException(
                    String.format("An error occurred while deleting cover image for project with ID: %d. Error: %s",
                            projectId, ex.getMessage()), ex);
        }

        project.setCoverImageId(null);
        projectService.saveProject(project);

        log.info("Project {} cover successfully deleted.", projectId);
    }

    public byte[] downloadProjectCover(Long userId, Long projectId) {
        projectValidator.validateProjectExistsById(projectId);
        projectValidator.validateUserIsProjectOwner(userId, projectId);

        Project project = projectService.getProjectById(projectId);
        byte[] coverAsBytes;

        try {
            coverAsBytes = storageService.downloadResource(project.getCoverImageId());
        } catch (S3Exception ex) {
            log.error("Failed to download cover for project ID: {}. Error: {}", projectId, ex.getMessage(), ex);
            throw new IllegalStateException(
                    String.format("An error occurred while downloading cover for project with ID: %d. Error: %s",
                            projectId, ex.getMessage()), ex);
        }

        log.info("Project {} cover successfully downloaded.", projectId);
        return coverAsBytes;
    }
}