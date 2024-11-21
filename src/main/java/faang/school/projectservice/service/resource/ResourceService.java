package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.dto.resource.ResourceRequestDto;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final AmazonS3 s3Client;
    private final ResourceRepository resourceRepository;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Transactional
    public List<String> upload(@Size(min = 1) List<MultipartFile> files,
                               @Valid ResourceRequestDto requestDto,
                               @Positive long userId) {
        TeamMember teamMember = teamMemberService.getTeamMemberByUserIdAndProjectId(userId, requestDto.getProjectId());
        if (teamMember == null) {
            log.error("TeamMember id={} not found in project id={}",
                    userId, requestDto.getProjectId());
            throw new IllegalArgumentException("TeamMember not found in project");
        }
        log.info("TeamMember id={} found in project id={}", userId, requestDto.getProjectId());

        Project project = projectService.getProjectById(requestDto.getProjectId());
        log.info("Project id={} found", requestDto.getProjectId());

        BigInteger filesSize = BigInteger.valueOf(files.stream()
                .mapToLong(MultipartFile::getSize)
                .sum()
        );
        BigInteger updateProjectStorageSize = project.getStorageSize().add(filesSize);
        if (updateProjectStorageSize.compareTo(project.getMaxStorageSize()) > 0) {
            log.error("Project id={} has exceeded its max storage size:" +
                            " updateProjectStorageSize={}, projectMaxStorageSize={}",
                    requestDto.getProjectId(),
                    updateProjectStorageSize,
                    project.getMaxStorageSize()
            );
            throw new IllegalArgumentException("Project has exceeded max storage size");
        }
        log.info("Project id={} storage size updated to {}", requestDto.getProjectId(), updateProjectStorageSize);

        List<String> fileKeys = new ArrayList<>();
        List<Resource> resources = new ArrayList<>();
        String folder = project.getId() + project.getName();
        files.forEach(file -> {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());
            String fileKey = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
            try {
                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        bucketName, fileKey, file.getInputStream(), objectMetadata
                );
                s3Client.putObject(putObjectRequest);
            } catch (IOException e) {
                log.error("File not found", e);
                throw new FileException("File not found");
            } catch (Exception e) {
                log.error("Error uploading file to S3", e);
                throw new FileException("Error uploading file to S3");
            }
            log.info("File uploaded to S3: {}", fileKey);

            resources.add(
                    Resource.builder()
                            .name(file.getOriginalFilename())
                            .key(fileKey)
                            .size(BigInteger.valueOf(file.getSize()))
                            .allowedRoles(teamMember.getRoles())
                            .type(ResourceType.getResourceType(file.getContentType()))
                            .status(ResourceStatus.ACTIVE)
                            .createdAt(LocalDateTime.now())
                            .createdBy(teamMember)
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
