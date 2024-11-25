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
        Project project = getProject(projectId);

        if (!teamMemberRepository.isUserInAnyTeamOfProject(projectId, userId)){
            throw new PermissionDeniedException("User with id: " + userId + " is not in the project with name: " + project.getName());
        }

        if(project.getStorageSize() == null){
            project.setStorageSize(BigInteger.ZERO);
        }

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSize(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        Resource resource = awsS3Service.uploadFile(folder, file);
        resource.setProject(project);

        resource = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);

        return resourceDtoMapper.toDto(resource);
    }

    @Transactional
    public ResourceDto updateResource(Long userId, Long resourceId, MultipartFile file){
        Resource existingResource = getResource(resourceId);
        Project existingProject = getProject(existingResource.getProject().getId());

        checkTeamMemberPermissionToManageResource(existingResource, userId);

        BigInteger existingStorageSize = existingProject.getStorageSize().subtract(existingResource.getSize());
        BigInteger updatedStorageSize = existingStorageSize.add(BigInteger.valueOf(file.getSize()));

        BigInteger newStorageSize = existingProject.getStorageSize().add(BigInteger.valueOf(file.getSize()));

        checkStorageSize(newStorageSize, existingProject.getMaxStorageSize());

        Resource updatedResource = awsS3Service.updateResource(existingResource.getKey(), file);
        existingResource.setProject(existingProject);
        existingResource.setKey(updatedResource.getKey());
        existingResource.setSize(updatedResource.getSize());
        existingResource.setName(file.getOriginalFilename());
        existingResource.setStatus(ResourceStatus.ACTIVE);
        existingResource.setType(updatedResource.getType());

        existingResource = resourceRepository.save(existingResource);

        existingProject.setStorageSize(updatedStorageSize);

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
        Resource resource = getResource(resourceId);
        Project project = getProject(resource.getProject().getId());
        checkTeamMemberPermissionToManageResource(resource, userId);

        BigInteger actualStorageSize = project.getStorageSize();
        project.setStorageSize(actualStorageSize.subtract(resource.getSize()));

        awsS3Service.deleteResource(resource.getKey());

        resource.setKey(null);
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);

        resourceRepository.save(resource);

        log.info("Resource with id {} was successfully deleted", resourceId);
    }

    private void checkTeamMemberPermissionToManageResource(Resource resource, Long userId){
        boolean hasPermission = teamMemberRepository.findByUserId(userId)
                .stream()
                .anyMatch(member ->
                        member.getId().equals(resource.getCreatedBy()) ||
                                member.getRoles().contains(TeamRole.MANAGER)
                );

        if (!hasPermission) {
            log.error("Team member does not have permission to manage this resource");
            throw new EntityNotFoundException("Invalid team member or role");
        }
    }

    private Project getProject(Long projectId) {
        return projectService.findProject(projectId).orElseThrow(
                () -> new EntityNotFoundException(PROJECT_NOT_FOUND));
    }

    private Resource getResource(Long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow(
                () -> new EntityNotFoundException(RESOURCE_NOT_FOUND));
    }

    private TeamMember getTeamMember(Long userId, Long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
    }

    private void checkStorageSize(BigInteger newFileSize, BigInteger maxStorageSize){
        if (newFileSize.compareTo(maxStorageSize) > 0) {
            log.error("Storage size is exceeded");
            throw new StorageSizeException("Storage size is exceeded");
        }
    }
}

