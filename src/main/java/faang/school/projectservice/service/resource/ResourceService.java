package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.PermissionDeniedException;
import faang.school.projectservice.exception.StorageSizeException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.resource.ResourceDtoMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.AwsS3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {
    private static final String PROJECT_NOT_FOUND = "Project not found";
    private static final String RESOURCE_NOT_FOUND = "Resource not found";
    private static final String TEAM_MEMBER_NOT_FOUND = "Team member not found";

    private final ResourceRepository resourceRepository;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final ProjectService projectService;
    private final AwsS3Service awsS3Service;
    private final ResourceDtoMapper resourceDtoMapper;

    @Transactional
    public ResourceDto uploadFile(Long projectId, Long userId, MultipartFile file) {
        Project project = getProjectOrThrow(projectId);
        TeamMember teamMember = getTeamMemberOrThrow(userId);

        if(project.getStorageSize() == null){
            project.setStorageSize(BigInteger.ZERO);
        }

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSize(project.getStorageSize(), newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        Resource resource = awsS3Service.uploadFile(folder, file);
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);

        resource = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        projectService.updateProject(project);

        return resourceDtoMapper.toDto(resource);
    }

    @Transactional
    public ResourceDto updateResource(Long userId, Long resourceId, MultipartFile file){
        Resource existingResource = getResourceOrThrow(resourceId);
        Project existingProject = getProjectOrThrow(existingResource.getProject().getId());

        TeamMember teamMember = checkPermissionToDeleteResource(existingResource, userId);

        BigInteger existingStorageSize = existingProject.getStorageSize().subtract(existingResource.getSize());
        BigInteger updatedStorageSize = existingStorageSize.add(BigInteger.valueOf(file.getSize()));

        BigInteger newStorageSize = existingProject.getStorageSize().add(BigInteger.valueOf(file.getSize()));

        checkStorageSize(existingProject.getStorageSize(), newStorageSize, existingProject.getMaxStorageSize());

        Resource updatedResource = awsS3Service.updateResource(existingResource.getKey(), file);
        existingResource.setProject(existingProject);
        existingResource.setKey(updatedResource.getKey());
        existingResource.setSize(updatedResource.getSize());
        existingResource.setName(file.getOriginalFilename());
        existingResource.setStatus(ResourceStatus.ACTIVE);
        existingResource.setType(updatedResource.getType());
        existingResource.setCreatedBy(teamMember);
        existingResource.setUpdatedBy(teamMember);

        existingResource = resourceRepository.save(existingResource);

        existingProject.setStorageSize(updatedStorageSize);
        projectService.updateProject(existingProject);

        return resourceDtoMapper.toDto(existingResource);
    }

    public InputStream downloadResource(Long resourceId){
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new EntityNotFoundException(RESOURCE_NOT_FOUND)
        );

        return awsS3Service.downloadFile(resource.getKey());
    }

    @Transactional
    public void deleteResource(Long resourceId, Long userId){
        Resource resource = getResourceOrThrow(resourceId);
        Project project = getProjectOrThrow(resource.getProject().getId());
        TeamMember teamMember = checkPermissionToDeleteResource(resource, userId);

        BigInteger actualStorageSize = project.getStorageSize();
        project.setStorageSize(actualStorageSize.subtract(resource.getSize()));

        awsS3Service.deleteResource(resource.getKey());

        resource.setKey(null);
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(teamMember);

        resourceRepository.save(resource);
        projectService.updateProject(project);

        log.info("Resource with id {} was successfully deleted", resourceId);
    }

    private TeamMember checkPermissionToDeleteResource(Resource resource, Long userId){
        TeamMember teamMember = teamMemberRepository.findByUserId(userId)
                .stream()
                .filter(member ->
                        member.getId().equals(resource.getCreatedBy().getId()) ||
                                member.getRoles().contains(TeamRole.MANAGER)
                )
                .findFirst()
                .orElse(null);

        if (teamMember == null) {
            log.error("Team member does not have permission to delete this resource");
            throw new EntityNotFoundException("Invalid team member or role");
        }

        return teamMember;
    }

    private Project getProjectOrThrow(Long projectId) {
        return projectService.findProject(projectId).orElseThrow(
                () -> new EntityNotFoundException(PROJECT_NOT_FOUND));
    }

    private Resource getResourceOrThrow(Long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow(
                () -> new EntityNotFoundException(RESOURCE_NOT_FOUND));
    }

    private TeamMember getTeamMemberOrThrow(Long userId) {
        return teamMemberRepository.findByUserId(userId).stream().findFirst().orElseThrow(
                () -> new PermissionDeniedException(TEAM_MEMBER_NOT_FOUND));
    }

    private void checkStorageSize(BigInteger currentSize, BigInteger newFileSize, BigInteger maxStorageSize){
        BigInteger totalSize = currentSize.add(newFileSize);

        if (totalSize.compareTo(maxStorageSize) > 0) {
            log.error("Storage size is exceeded");
            throw new StorageSizeException("Storage size is exceeded");
        }
    }
}

