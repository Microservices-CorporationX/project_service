package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final static long TWO_GB_IN_BYTES = 2_147_483_648L;
    private final static String EXCEEDING_MAXIMUM_STORAGE_SIZE = "Exceeding maximum storage size!!!";
    private final static String RESOURCE_NOT_FOUND_BY_ID = "Resource with id = %s not found";
    private final static String RESOURCE_KEY_IS_NULL = "Resource key is null for resourceId = %s";
    private final static String TEAM_MEMBER_NOT_FOUND = "Team member with userId = %s and projectId = %s not found";
    private final static String CHECK_PERMISSIONS_ERROR = "Check permissions error";

    private final ResourceRepository resourceRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final UserValidator userValidator;
    private final ResourceMapper resourceMapper;

    @Transactional
    public InputStream downloadResource(Long resourceId) {
        Resource resource = getResourceById(resourceId);
        return s3Service.downloadFile(resource.getKey());
    }

    @Transactional
    public long deleteResource(Long resourceId, Long userId) {
        userValidator.validateUserId(userId);

        Resource resource = getResourceById(resourceId);
        TeamMember teamMember = getResourceWithCheckedPermissions(resource, userId);
        Project project = resource.getProject();

        BigInteger newStorageSize = project.getStorageSize().subtract(resource.getSize());
        String resourceKey = getResourceKey(resourceId);

        s3Service.deleteFile(resourceKey);

        LocalDateTime now = LocalDateTime.now();
        resource.setSize(BigInteger.valueOf(0));
        resource.setKey("");
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedAt(now);
        resource.setUpdatedBy(teamMember);
        Resource resourceSaved = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        return resourceSaved.getId();
    }

    @Transactional
    public ResourceDto updateResource(Long resourceId, Long userId, MultipartFile file) {
        userValidator.validateUserId(userId);

        Resource resource = getResourceById(resourceId);
        TeamMember teamMember = getResourceWithCheckedPermissions(resource, userId);
        Project project = resource.getProject();

        BigInteger newStorageSize = project.getStorageSize()
                .add((BigInteger.valueOf(file.getSize())))
                .subtract(resource.getSize());

        checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        s3Service.deleteFile(resource.getKey());
        String key = s3Service.uploadFile(file, folder);
        LocalDateTime now = LocalDateTime.now();

        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setUpdatedAt(now);
        resource.setName(file.getOriginalFilename());
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setUpdatedBy(teamMember);
        Resource resourceSaved = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        return resourceMapper.toDto(resourceSaved);
    }

    @Transactional
    public ResourceDto addResource(Long projectId, Long userId, MultipartFile file) {
        userValidator.validateUserId(userId);

        TeamMember teamMember = getTeamMemberByUserIdAndProjectId(userId, projectId);

        Project project = projectRepository.getProjectById(projectId);
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        String key = s3Service.uploadFile(file, folder);
        LocalDateTime now = LocalDateTime.now();


        Resource resource = Resource.builder()
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .createdAt(now)
                .updatedAt(now)
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType(file.getContentType()))
                .name(file.getOriginalFilename())
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .allowedRoles(new ArrayList<>(teamMember.getRoles()))
                .build();

        resource.setProject(project);
        resource = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        return resourceMapper.toDto(resource);
    }

    private void checkStorageSizeExceeded(BigInteger newStorageSize, BigInteger maxStorageSize) {
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            log.error(EXCEEDING_MAXIMUM_STORAGE_SIZE);
            throw new ValidationException(EXCEEDING_MAXIMUM_STORAGE_SIZE);
        }
    }

    private TeamMember getResourceWithCheckedPermissions(Resource resource, long userId) {
        Long projectId = resource.getProject().getId();
        TeamMember teamMember = getTeamMemberByUserIdAndProjectId(userId, projectId);

        if (isResourceOwner(resource, teamMember) || isManager(teamMember)) {
            return teamMember;
        }

        throw new ValidationException(CHECK_PERMISSIONS_ERROR);
    }

    private boolean isResourceOwner(Resource resource, TeamMember teamMember) {
        return resource.getCreatedBy() != null && teamMember.getId().equals(resource.getCreatedBy().getId());
    }

    private boolean isManager(TeamMember teamMember) {
        if (teamMember.getRoles() == null || teamMember.getRoles().size() == 0) {
            return false;
        }
        return teamMember.getRoles().stream().anyMatch(r -> r.equals(TeamRole.MANAGER));
    }

    private Resource getResourceById(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() ->
                new EntityNotFoundException(String.format(RESOURCE_NOT_FOUND_BY_ID, resourceId)));
        return resource;
    }

    private TeamMember getTeamMemberByUserIdAndProjectId(long userId, long projectId) {
        TeamMember teamMember = teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);
        if (teamMember == null) {
            throw new EntityNotFoundException(String.format(TEAM_MEMBER_NOT_FOUND, userId, projectId));
        }
        return teamMember;
    }

    private String getResourceKey(long resourceId) {
        return resourceRepository.getResourceKeyById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(RESOURCE_KEY_IS_NULL, resourceId)));

    }
}
