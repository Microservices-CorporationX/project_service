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
    public void uploadProjectCover(MultipartFile file, long userId, long projectId) {
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
        storageService.uploadResource(coverFile, key);

        project.setCoverImageId(key);
        projectService.saveProject(project);

        log.info("Project {} cover successfully uploaded.", projectId);
    }

    @Transactional
    public void deleteProjectCover(long userId, long projectId) {
        projectValidator.validateProjectExistsById(projectId);
        projectValidator.validateUserIsProjectOwner(userId, projectId);

        Project project = projectService.getProjectById(projectId);

        if (!projectValidator.hasCoverImage(project)) {
            throw new IllegalStateException(String.format("Project %d has no cover image to delete.", projectId));
        }

        String key = project.getCoverImageId();
        storageService.deleteResource(key);
        project.setCoverImageId(null);
        projectService.saveProject(project);

        log.info("Project {} cover successfully deleted.", projectId);
    }

    public byte[] downloadProjectCover(long userId, long projectId) {
        projectValidator.validateProjectExistsById(projectId);
        projectValidator.validateUserIsProjectOwner(userId, projectId);

        Project project = projectService.getProjectById(projectId);
        byte[] coverAsBytes = storageService.downloadResource(project.getCoverImageId());

        log.info("Project {} cover successfully downloaded.", projectId);
        return coverAsBytes;
    }
}