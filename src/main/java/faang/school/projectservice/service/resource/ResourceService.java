package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.team.TeamServiceImpl;
import faang.school.projectservice.validator.resource.ValidatorForResourceService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {
    private final ProjectService projectService;
    private final S3Service s3Service;
    private final UserContext userContext;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final TeamServiceImpl teamService;

    @Transactional
    public ResourceDto addResource(long projectId, MultipartFile multipartFile) {
        log.info("getting project from db and validate for enough memory");
        Project project = projectService.getProjectEntityById(projectId);
        BigInteger fileSize = BigInteger.valueOf(multipartFile.getSize());

        BigInteger storageSize = project.getStorageSize().add(fileSize);
        ValidatorForResourceService.checkStorageForEnoughMemory(storageSize, project.getMaxStorageSize());
        project.setStorageSize(storageSize);

        String key = String.format("%s/%d %s", projectId + project.getName(), System.currentTimeMillis(), multipartFile.getOriginalFilename());

        log.info("getting author from db");
        TeamMember author = getTeamMember(projectId);

        log.info("calling s3 to add resource to cloud");
        Resource resource = s3Service.addResource(multipartFile, key);

        resource.setProject(project);
        resource.setCreatedBy(author);
        resource.setUpdatedBy(author);

        log.info("update resource and project");
        save(resource);
        updateProject(project);

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public ResourceDto updateResource(long resourceId, MultipartFile multipartFile) {
        log.info("getting resource and project from db");
        Resource resource = getResource(resourceId);
        Project project = resource.getProject();

        BigInteger fileSize = BigInteger.valueOf(multipartFile.getSize());

        BigInteger storageSize = project.getStorageSize().add(fileSize);
        storageSize = storageSize.subtract(resource.getSize());

        log.info("validate resource for enough memory");
        ValidatorForResourceService.checkStorageForEnoughMemory(storageSize, project.getMaxStorageSize());

        project.setStorageSize(storageSize);
        log.info("getting teamMember from db");
        TeamMember updatedBy = getTeamMember(project.getId());

        resource.setUpdatedBy(updatedBy);
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setSize(BigInteger.valueOf(multipartFile.getSize()));

        log.info("update resource and project");
        save(resource);
        updateProject(project);

        log.info("calling s3 for update Resource at cloud");
        s3Service.updateResource(multipartFile, resource.getKey());
        return resourceMapper.toDto(resource);
    }

    @Transactional
    public ResourceDto removeResource(long resourceId) {
        log.info("getting resource and project from db");
        Resource resource = getResource(resourceId);
        Project project = resource.getProject();

        log.info("getting userId from userContext");
        long userId = userContext.getUserId();

        log.info("getting teamMember from bd");
        TeamMember teamMember = getTeamMember(project.getId());

        log.info("validate for invalid access");
        ValidatorForResourceService.checkAccessForRemoval(userId, resource, teamMember.getRoles());

        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setUpdatedBy(teamMember);

        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));
        resource.setSize(BigInteger.valueOf(0));

        log.info("calling s3 for remove resource from cloud");
        s3Service.completeRemoval(resource.getKey());
        resource.setKey(null);

        log.info("update resource and project");
        save(resource);
        updateProject(project);

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public String generatePresignedUrl(@Min(0) long resourceId){
        log.info("getting resource from db");
        Resource resource = getResource(resourceId);
        log.info("calling s3 to generate presignedUrl");
        return s3Service.generatePresignedUrl(resource.getKey());
    }


    private void save(@NotNull Resource resource) {
        log.info("save resource");
        resourceRepository.save(resource);
    }

    private void updateProject(@NotNull Project project) {
        log.info("save project");
        projectService.saveProject(project);
    }

    private TeamMember getTeamMember(@Min(0) long projectId) {
        log.info("using team service to find TeamMember");
        return teamService.findMemberByUserIdAndProjectId(userContext.getUserId(), projectId)
                .orElseThrow(() -> new EntityNotFoundException("Такого участника команды не существует"));
    }

    private Resource getResource(@Min(0) long resourceId) {
        log.info("calling findById on resourceRepository");
        return resourceRepository.findById(resourceId)
                .orElseThrow(EntityNotFoundException::new);
    }

}
