package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.SizeExceeded;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final S3Service s3Service;
    private final ProjectRepository projectRepository;
    private final ResourceMapper resourceMapper;
    private final UserServiceClient userServiceClient;
    private final ResourceRepository resourceRepository;
    private final TeamMemberJpaRepository teamMemberDpaRepository;

    @Transactional
    public ResourceDto addResource(Long projectId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(project.getMaxStorageSize(), newStorageSize);
        log.info("The file {} has been successfully verified for add", file.getOriginalFilename());

        String folder = project.getId() + project.getName();
        Resource resource = s3Service.uploadFile(file, folder);

        resource.setProject(project);
        resource = resourceRepository.save(resource);
        log.info("The resource with {}id has been successfully saved in repository", resource.getId());

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);
        log.info("The project with {}id has been successfully saved in repository", project.getId());

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public InputStream downloadResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource with " + resourceId + " id not found"));
        return s3Service.downloadFile(resource.getKey());
    }

    @Transactional
    public void deleteResource(long resourceId, long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new EntityNotFoundException("User with " + userId + " id not found");
        }

        Resource resource = resourceRepository.getReferenceById(resourceId);
        String key = resource.getKey();

        s3Service.deleteFile(key);
        resourceRepository.delete(resource);

        Project project = resource.getProject();
        BigInteger newSize = project.getStorageSize().subtract(resource.getSize());
        project.setStorageSize(newSize);
    }

    @Transactional(readOnly = true)
    public List<ResourceDto> getAvailableResources(long projectId, Long userId) {
        TeamMember teamMember = teamMemberDpaRepository.findByUserIdAndProjectId(userId, projectId);
        Project project = projectRepository.getProjectById(projectId);
//        return getAllAvailable(project, teamMember);
        return null;
    }

//    private List<ResourceDto> getAllAvailable(Project project, TeamMember teamMember) {
//        // Логика для получения всех доступных ресурсов для проекта и участника команды
////        List<Resource> resources = resourceRepository.findAllByProjectAndTeamMember(project, teamMember);
//        return resourceMapper.toDto(resources);
//    }

    @Transactional
    public ResourceDto updateResource(Long resourceId, Long userId, MultipartFile file) {
        Resource resourceFromDB = getResourceWithCheckedPermissions(resourceId, userId);
        log.info("User with {}id has permissions to update the file", userId);

        Project project = resourceFromDB.getProject();

        BigInteger newStorageSize = project.getStorageSize()
                .add(BigInteger.valueOf(file.getSize()))
                .subtract(resourceFromDB.getSize());

        checkStorageSizeExceeded(project.getMaxStorageSize(), newStorageSize);
        String folder = project.getId() + project.getName();
        s3Service.deleteFile(resourceFromDB.getKey());

        Resource resource = s3Service.uploadFile(file, folder);
        resourceFromDB.setKey(resource.getKey());
        resourceFromDB.setSize(resource.getSize());
        resourceFromDB.setUpdatedAt(resource.getUpdatedAt());
        resourceFromDB.setName(resource.getName());
        resourceFromDB.setType(resource.getType());

        resourceRepository.save(resourceFromDB);
        log.info("The resource with {} id has been successfully update", resourceFromDB.getId());

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);
        log.info("The project with {} id has been successfully update", project.getId());

        return resourceMapper.toDto(resourceFromDB);
    }

    private void checkStorageSizeExceeded(BigInteger maxStorageSize, BigInteger newStorageSize) {
        if (0 > maxStorageSize.compareTo(newStorageSize)) {
            throw new SizeExceeded(ErrorMessage.FILE_STORAGE_CAPACITY_EXCEEDED);
        }
    }

    private Resource getResourceWithCheckedPermissions(Long resourceId, Long userDtoId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Resource with %s id not found", resourceId)));
        var teamMember = resource.getCreatedBy();
        if (teamMember.getUserId().equals(userDtoId)) {
            return resource;
        } else {
            throw new IllegalArgumentException(String.format("User with %sid don't have permissions to update the file with %did", userDtoId, resourceId));
        }
    }
}
