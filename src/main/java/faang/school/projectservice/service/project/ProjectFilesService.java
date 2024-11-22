package faang.school.projectservice.service.project;

import faang.school.projectservice.exception.StorageExceededException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.amazons3.S3Service;
import faang.school.projectservice.service.teammember.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectFilesService {

    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;

    public void uploadFile(long projectId, long teamMemberId, MultipartFile file) {
        Project project = projectService.getProjectById(projectId);
        BigInteger maxStorageSize = new BigInteger("2147483648");
        project.setMaxStorageSize(maxStorageSize);

        BigInteger currentStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(maxStorageSize, currentStorageSize);

        String folder = projectId + project.getName();
        TeamMember fileCreator = teamMemberService.findById(teamMemberId);

        String key = s3Service.uploadFile(file, folder);

        Resource resource = Resource.builder()
                .name(file.getOriginalFilename())
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(fileCreator.getRoles())
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

    private void checkStorageSizeExceeded(BigInteger maxStorageSize,
                                          BigInteger currentStorageSize) {
        if (maxStorageSize.compareTo(currentStorageSize) < 0) {
            throw new StorageExceededException("Storage can't exceed 2 Gb ");
        }
    }
}
