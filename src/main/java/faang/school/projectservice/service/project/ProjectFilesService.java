package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.amazons3.S3Service;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.validator.resource.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectFilesService {

    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceValidator resourceValidator;
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final TeamMemberService teamMemberService;

    public void uploadFile(long projectId, long teamMemberId, MultipartFile file) {
        Project project = projectService.getProjectById(projectId);
        BigInteger maxStorageSize = project.getMaxStorageSize();
        BigInteger currentStorageSize = project.getStorageSize().
                add(BigInteger.valueOf(file.getSize()));
        project.setStorageSize(currentStorageSize);

        resourceValidator.checkMaxStorageSizeIsNotNull(maxStorageSize);
        resourceValidator.checkStorageSizeNotExceeded(maxStorageSize, currentStorageSize);
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

        projectService.updateProject(projectMapper.toDto(project));
        resourceRepository.save(resource);
        log.info("Saving new Resource with key: {}, with status: {}",
                resource.getKey(), resource.getStatus());
    }

    public InputStream downloadFile(long resourceId) {
        Resource resource = getResource(resourceId);
        String key = resource.getKey();

        return s3Service.downloadFile(key);
    }

    public Map<String, InputStream> downloadAllFiles(long projectId) {
        Project project = projectService.getProjectById(projectId);
        Map<String, String> filesNameWithKey = new HashMap<>();
        project.getResources().stream()
                .filter(resource -> resource.getStatus().equals(ResourceStatus.ACTIVE))
                .forEach(resource -> filesNameWithKey.put(resource.getId() + resource.getName(), resource.getKey()));

        Map<String, S3ObjectInputStream> s3ObjectInputStreams = s3Service.downloadAllFiles(filesNameWithKey);

        Map<String, InputStream> files = new HashMap<>();
        s3ObjectInputStreams.forEach((key, value) -> files.put(key, value.getDelegateStream()));
        return files;
    }

    public void deleteFile(long resourceId, long teamMemberId) {
        Resource resource = getResource(resourceId);
        TeamMember teamMember = teamMemberService.findById(teamMemberId);
        Project project = projectService.getProjectById(resource.getProject().getId());

        resourceValidator.validateAllowedToDeleteFile(resource, teamMember);

        String key = resource.getKey();
        s3Service.deleteFile(key);

        BigInteger renewStorageSize = project.getStorageSize().subtract(resource.getSize());
        project.setStorageSize(renewStorageSize);

        resource.setKey(null);
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setUpdatedBy(teamMember);

        projectService.updateProject(projectMapper.toDto(project));
        resourceRepository.save(resource);
        log.info("Saving Resource with id: {} , with status: {} ",
                resource.getId(), resource.getStatus());
    }

    private Resource getResource(long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow(
                () -> new EntityNotFoundException("Resource", resourceId));
    }
}
