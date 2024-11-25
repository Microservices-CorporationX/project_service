package faang.school.projectservice.service.project;

import faang.school.projectservice.exception.project.StorageSizeExceededException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;

    public Optional<Project> findProject(Long id){
        if (id == null){
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

    private void checkStorageSize(BigInteger newStorageSize, BigInteger maxStorageSize) {
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            log.error("Received a request to upload an image that exceeds the total storage");
            throw new StorageSizeExceededException("Storage size exceeded! Choose a smaller image.");
        }
    }
}
