package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.utilities.ImageConvert;
import jakarta.validation.ValidationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Getter
public class ProjectCoverService {
    private final S3Service s3Service;
    private final ProjectRepository projectRepository;
    private final ImageConvert imageConvert;

    @Value("${file.folder.project-cover-name}")
    private String folder;
    @Value("${services.s3.bucket-name}")
    private String bucketName;
    @Value("${file.extension.target-image-size}")
    private int targetImageSize;

    @Transactional
    public ResourceDto add(Long projectId, MultipartFile file) {
        log.info("Start add/update a cover for the project: {}", projectId);

        Project project = projectRepository.getProjectById(projectId);
        String oldKey = project.getCoverImageId();
        String key = String.format("%s/%s", folder, s3Service.getKeyName());

        try {
            s3Service.toS3File(bucketName,
                    key,
                    file.getContentType(),
                    imageConvert.resizeImageJpg(file.getInputStream(), targetImageSize));
        } catch (IOException e) {
            log.error("Error transformation file from MultipartFile to InputStream for the project: {}. Error Message:\n {}", projectId, e);
            throw new RuntimeException(String.format("Error transformation file from MultipartFile to InputStream for the project: %s", projectId));
        }

        project.setCoverImageId(key);
        projectRepository.save(project);
        if (oldKey != null) {
            log.info("Delete a cover for the project: {}. Id was: {} ", projectId, oldKey);
            s3Service.deleteFile(bucketName, oldKey);
        }
        return new ResourceDto(key);
    }

    public InputStream upload(Long idProject) {
        log.info("Start upload a cover for the project: {}", idProject);

        Project project = projectRepository.getProjectById(idProject);
        return s3Service.fromS3File(bucketName, project.getCoverImageId());
    }

    public ResourceDto delete(Long idProject) {
        log.info("Start delete a cover for the project: {}", idProject);

        Project project = projectRepository.getProjectById(idProject);
        if (project.getCoverImageId() != null) {
            s3Service.deleteFile(bucketName, project.getCoverImageId());
        } else {
            String message = String.format("The KeyId is Null for the project id %d", idProject);
            log.error(message);
            throw new ValidationException(message);
        }
        return new ResourceDto(project.getCoverImageId());
    }
}