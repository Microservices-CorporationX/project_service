package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.team.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {
    private static final String RESOURCE_FOLDER_NAME = "project-%d-files";

    private final ProjectService projectService;
    private final S3Service s3Service;
    private final UserContext userContext;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final TeamMemberService teamMemberService;

    public ResourceDto addResource(long projectId, MultipartFile file) {
        long userId = userContext.getUserId();
        TeamMember teamMember = teamMemberService.validateUserIsProjectMember(userId, projectId);
        Project project = projectService.changeStorageSize(projectId, file.getSize());
        String folder = String.format(RESOURCE_FOLDER_NAME, projectId);
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
        resource.setAllowedRoles(new ArrayList<>(teamMember.getRoles()));
        resource = resourceRepository.save(resource);
        return resourceMapper.toDto(resource);
    }

    public InputStream downloadResource(long resourceId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        long userId = userContext.getUserId();
        teamMemberService.validateUserIsProjectMember(userId, resource.getProject().getId());
        return s3Service.downloadFile(resource.getKey());
    }

    public List<ResourceDto> getProjectResources(long projectId) {
        long userId = userContext.getUserId();
        teamMemberService.validateUserIsProjectMember(userId, projectId);
        return resourceMapper.toDtoList(resourceRepository.findAllByProjectId(projectId));
    }

    public void deleteResource(long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
        long userId = userContext.getUserId();
        TeamMember teamMember = teamMemberService.validateUserIsProjectMember(userId, resource.getProject().getId());
        if (canChangeThisResource(teamMember, resource)) {
            resource.setStatus(ResourceStatus.DELETED);
            resource.setUpdatedAt(LocalDateTime.now());
            resource.setUpdatedBy(teamMember);
            resourceRepository.save(resource);
        } else {
            log.info("User with id {} tried to delete resource {} but access denied", userId, resource);
            throw new AccessDeniedException("Need to be manager or file creator to delete file");
        }
    }

    public void updateResource(long resourceId, MultipartFile file) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
        long userId = userContext.getUserId();
        TeamMember teamMember = teamMemberService.validateUserIsProjectMember(userId, resource.getProject().getId());
        if (canChangeThisResource(teamMember, resource)) {
            Project project = projectService.changeStorageSize(resource.getProject().getId(),
                    BigInteger.valueOf(file.getSize()).subtract(resource.getSize()).longValue());
            String folder = String.format(RESOURCE_FOLDER_NAME, project.getId());
            Resource resourceFromS3 = s3Service.uploadFile(file, folder);
            resource.setKey(resourceFromS3.getKey());
            resource.setUpdatedBy(teamMember);
            resource.setUpdatedAt(LocalDateTime.now());
            resourceRepository.save(resource);
        }
    }

    private boolean canChangeThisResource(TeamMember teamMember, Resource resource) {
        List<TeamRole> roles = teamMember.getRoles();
        return (roles != null && roles.contains(TeamRole.MANAGER)) || resource.getCreatedBy().equals(teamMember);
    }
}
