package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.FileDownloadException;
import faang.school.projectservice.exception.StorageExceededException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.amazons3.S3Service;
import faang.school.projectservice.service.teammember.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectFilesService {

    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;

    public void uploadFile(long projectId, long teamMemberId, MultipartFile file) {
        Project project = projectService.getProjectById(projectId);
        BigInteger maxStorageSize = project.getMaxStorageSize();
        BigInteger currentStorageSize = project.getStorageSize().
                add(BigInteger.valueOf(file.getSize()));

        checkMaxStorageSizeIsNotNull(maxStorageSize);
        checkStorageSizeNotExceeded(maxStorageSize, currentStorageSize);
        String folder = projectId + project.getName();

        String key = s3Service.uploadFile(file, folder);

        TeamMember fileCreator = teamMemberService.findById(teamMemberId);
        ArrayList<TeamRole> allowedRoles = new ArrayList<>(fileCreator.getRoles());

        Resource resource = Resource.builder()
                .name(file.getOriginalFilename())
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(allowedRoles)
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .createdBy(fileCreator)
                .updatedBy(fileCreator)
                .updatedAt(LocalDateTime.now())
                .project(project)
                .build();

        resourceRepository.save(resource);
    }

    public byte[] downloadFile(long resourceId) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new EntityNotFoundException("Resource", resourceId));
        String key = resource.getKey();

        try (S3ObjectInputStream inputStream = s3Service.downloadFile(key)) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new FileDownloadException("Error reading content" +
                    Arrays.toString(e.getStackTrace()));
        }
    }

    public void deleteFile(long resourceId) {
        s3Service.deleteFile(key);
    }

    private void checkMaxStorageSizeIsNotNull(BigInteger maxStorageSize) {
        if (maxStorageSize == null) {
            throw new IllegalStateException("Max storage size is not set for the project.");
        }
    }

    private void checkStorageSizeNotExceeded(BigInteger maxStorageSize,
                                             BigInteger currentStorageSize) {
        if (maxStorageSize.compareTo(currentStorageSize) < 0) {
            throw new StorageExceededException("Storage can't exceed 2 Gb ");
        }
    }
}
