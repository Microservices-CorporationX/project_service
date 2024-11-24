package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.RequestDeleteResourceDto;
import faang.school.projectservice.dto.resource.ResponseResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Util;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import faang.school.projectservice.validator.resource.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
@ConditionalOnProperty(value = "service.s3", havingValue = "true")
public class ResourceService {
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final S3Util s3Util;
    private final ResourceRepository resourceRepository;
    private final ResourceValidator resourceValidator;
    private final ResourceMapper resourceMapper;

    @Transactional
    public List<ResponseResourceDto> uploadResources(@Size(min = 1) List<MultipartFile> files,
                                                     @Positive long projectId,
                                                     @Positive long userId) {
        TeamMember teamMember = teamMemberService.getTeamMemberByUserIdAndProjectId(userId, projectId);
        log.info("TeamMember id={} found in project id={}", userId, projectId);

        Project project = projectService.getProject(projectId);
        log.info("Project id={} found", projectId);

        BigInteger filesSize = BigInteger.valueOf(files.stream()
                .mapToLong(MultipartFile::getSize)
                .sum()
        );
        BigInteger updateProjectStorageSize = project.getStorageSize().add(filesSize);
        resourceValidator.checkProjectStorageSizeExceeded(updateProjectStorageSize, project);
        log.info("Project id={} storage size updated to {}", projectId, updateProjectStorageSize);

        List<Resource> resources = new ArrayList<>();
        String folder = project.getId() + project.getName();
        files.forEach(file -> {
            String fileKey = String.format("%s/%s", folder, file.getOriginalFilename());
            s3Util.s3UploadFile(file, fileKey);
            resources.add(
                    Resource.builder()
                            .name(file.getOriginalFilename())
                            .key(fileKey)
                            .size(BigInteger.valueOf(file.getSize()))
                            .allowedRoles(new ArrayList<>(teamMember.getRoles()))
                            .type(ResourceType.getResourceType(file.getContentType()))
                            .status(ResourceStatus.ACTIVE)
                            .createdAt(LocalDateTime.now())
                            .createdBy(teamMember)
                            .updatedBy(teamMember)
                            .project(project)
                            .build()
            );
        });
        List<Resource> savedResources = resourceRepository.saveAll(resources);
        log.info("Resources count={} saved to DB", savedResources.size());
        project.getResources().addAll(savedResources);
        projectService.saveProject(project);
        log.info("Project id={} updated in DB", project.getId());
        return savedResources.stream()
                .map(resourceMapper::toDtoFromEntity)
                .toList();
    }

    @Transactional
    public void deleteResource(@Positive long userId, @Valid RequestDeleteResourceDto dto) {
        Resource resource = getResource(dto.getId());
        log.info("Resource id={} found", resource.getId());
        TeamMember teamMember = resource.getCreatedBy();
        Project project = resource.getProject();
        resourceValidator.checkUserInProject(userId, teamMember, project);
        log.info("User id={} can delete resource id={}", userId, resource.getId());

        s3Util.s3DeleteFile(resource.getKey());
        log.info("Resource id={} deleted from S3", resource.getId());

        BigInteger updateProjectStorageSize = project.getStorageSize().subtract(resource.getSize());
        if (updateProjectStorageSize.compareTo(BigInteger.ZERO) < 0) {
            updateProjectStorageSize = BigInteger.ZERO;
        }
        project.setStorageSize(updateProjectStorageSize);
        projectService.saveProject(project);
        log.info("Project id={} updated", project.getId());

        resource.setStatus(ResourceStatus.DELETED);
        resource.setKey(null);
        resource.setSize(null);
        resource.setUpdatedBy(teamMember);
        resource.setUpdatedAt(LocalDateTime.now());
        resourceRepository.save(resource);
        log.info("Resource id={} status updated to deleted", resource.getId());
    }

    public Resource getResource(@Positive long id) {
        return resourceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Resource not found by id: " + id)
        );
    }

    @Transactional
    public ResponseResourceDto updateResource(@NotNull MultipartFile file,
                                              @Positive long resourceId,
                                              @Positive long userId) {
        Resource resource = getResource(resourceId);
        Project project = resource.getProject();
        TeamMember teamMember = teamMemberService.getTeamMemberByUserIdAndProjectId(userId, project.getId());

        BigInteger subtractProjectStorageSize = project.getStorageSize().subtract(resource.getSize());
        BigInteger updateProjectStorageSize = subtractProjectStorageSize.add(BigInteger.valueOf(file.getSize()));
        resourceValidator.checkProjectStorageSizeExceeded(updateProjectStorageSize, project);

        s3Util.s3DeleteFile(resource.getKey());
        String fileKey = String.format("%s/%d%s", project.getId(), System.currentTimeMillis(), file.getOriginalFilename());
        s3Util.s3UploadFile(file, fileKey);

        projectService.saveProject(project);

        resource.setKey(fileKey);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setUpdatedBy(teamMember);
        resource.setUpdatedAt(LocalDateTime.now());
        resourceRepository.save(resource);
        log.info("Resource id={} updated in DB", resource.getId());
        return resourceMapper.toDtoFromEntity(resource);
    }
}
