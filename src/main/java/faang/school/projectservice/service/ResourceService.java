package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.ResourceValidator;
import faang.school.projectservice.validator.TeamMemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ResourceValidator resourceValidator;
    private final TeamMemberValidator teamMemberValidator;
    private final ProjectValidator projectValidator;
    private final TeamMemberService teamMemberService;
    private final StorageService storageService;
    private final ProjectService projectService;

    @Transactional
    public ResourceResponseDto uploadResource(Long projectId, Long userId, MultipartFile file) {
        projectValidator.validateProjectExistsById(projectId);
        teamMemberValidator.validateTeamMemberExistsById(userId);
        resourceValidator.validateResourceNotEmpty(file);

        Project project = projectService.getProjectById(projectId);
        projectValidator.validateUserInProjectTeam(userId, project);
        resourceValidator.validateEnoughSpaceInStorage(project, file);

        String folderName = String.format("%d_%s", projectId, project.getName());
        String key = String.format("%s/%s_%d", folderName, file.getOriginalFilename(), System.currentTimeMillis());

        Resource resource = createNewUploadResource(file, key, userId, project);

        storageService.uploadResourceAsync(file, key);

        projectService.updateStorageSizeAfterFileUpload(project, file);

        resource = resourceRepository.save(resource);

        return resourceMapper.toDto(resource);
    }

    private Resource createNewUploadResource(MultipartFile file, String key, Long userId, Project project) {
        List<TeamRole> allowedRoles = new ArrayList<>(teamMemberService.getTeamMemberByUserId(userId).getRoles());

        return Resource.builder()
                .name(file.getName())
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(allowedRoles)
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .createdBy(teamMemberService.getTeamMemberByUserId(userId))
                .updatedAt(LocalDateTime.now())
                .updatedBy(teamMemberService.getTeamMemberByUserId(userId))
                .project(project)
                .build();
    }
}
