package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.ResourceService;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;
    private final ResourceValidator resourceValidator;

    @Transactional
    @Override
    public ResourceResponseDto addResource(Long userId, Long projectId, MultipartFile file) {
        resourceValidator.validateUserInProject(userId, projectId);
        resourceValidator.validateResourcesOversize(projectId);
        String folder = "project_" + projectId;
        Resource resource = s3Service.uploadFile(file, folder);
        resource = resourceRepository.save(resource);
        return resourceMapper.toResourceResponseDto(resource);
    }

    @Override
    public InputStream downloadResource(Long userId, Long resourceId) {
        resourceValidator.validateUserCanDownloadResource(userId, resourceId);
        Resource resource = getResourceById(resourceId);
        return s3Service.downloadFile(resource.getKey());
    }

    @Transactional
    @Override
    public void deleteResource(Long userId, Long resourceId) {
        Resource resource = getResourceById(resourceId);
        resourceValidator.validateUserInProject(userId, resource.getProject().getId());
        resourceRepository.deleteById(resourceId);
        String key = resource.getKey();
        s3Service.deleteFile(key);
    }

    private Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow();
    }

}
