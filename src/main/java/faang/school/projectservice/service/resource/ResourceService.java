package faang.school.projectservice.service.resource;

import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Util;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import faang.school.projectservice.validator.resource.ResourceValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ResourceService {
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final S3Util s3Util;
    private final ResourceRepository resourceRepository;
    private final ResourceValidator resourceValidator;

    @Transactional
    public List<String> upload(@Size(min = 1) List<MultipartFile> files,
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
        resourceValidator.validateProjectStorageSizeExceeded(updateProjectStorageSize, project);
        log.info("Project id={} storage size updated to {}", projectId, updateProjectStorageSize);

        List<String> fileKeys = new ArrayList<>();
        List<Resource> resources = new ArrayList<>();
        String folder = project.getId() + project.getName();
        files.forEach(file -> {
            String fileKey = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
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
            fileKeys.add(fileKey);
        });

        List<Resource> savedResources = resourceRepository.saveAll(resources);
        log.info("Resources saved to DB: {}", savedResources.size());
        project.getResources().addAll(savedResources);
        projectService.saveProject(project);
        log.info("Project updated in DB: {}", project.getId());

        return fileKeys;
    }
}
