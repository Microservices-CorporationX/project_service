package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final S3Service s3Service;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final UserContext userContext;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    @Override
    public ResourceDto uploadFile(long projectId, MultipartFile file) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Project with id = %d not found", projectId)));
        BigInteger newStorageSize = BigInteger.valueOf(project.getStorageSize().longValue())
                .add(BigInteger.valueOf(file.getSize()));
        if (newStorageSize.compareTo(project.getMaxStorageSize()) > 0) {
            throw new IllegalArgumentException();
        }
        Resource resource = s3Service.uploadFile(file, project.getName());
        resource.setSize(newStorageSize);
        TeamMember member = teamMemberRepository.findByUserIdAndProjectId(userContext.getUserId(), project.getId());
        resource.setCreatedBy(member);
        resource.setProject(project);
        resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);

        return resourceMapper.toDto(resource);
    }

    @Override
    public InputStream downloadFile(String key) {
        resourceRepository.findByKey(key).orElseThrow(() ->
                new EntityNotFoundException(String.format("Resource with key = %s not found", key)));
        return s3Service.downloadFile(key);
    }

    @Transactional
    @Override
    public void deleteFile(String key) {
        Resource resource = resourceRepository.findByKey(key).orElseThrow(() ->
                new EntityNotFoundException(String.format("Resource with key = %s not found", key)));

        Project project = resource.getProject();
        long userId = userContext.getUserId();
        TeamMember member = teamMemberRepository.findByUserIdAndProjectId(userId, project.getId());

        if (member.getRoles().contains(TeamRole.MANAGER) || resource.getCreatedBy().equals(member)) {
            s3Service.deleteFile(key);

            BigInteger newStorageSize =
                    BigInteger.valueOf(project.getStorageSize().longValue())
                            .add(resource.getSize());
            project.setStorageSize(newStorageSize);
            project.setUpdatedAt(LocalDateTime.now());
            projectRepository.save(project);

            resource.setKey(null);
            resource.setSize(null);
            resource.setStatus(ResourceStatus.DELETED);
            resource.setUpdatedAt(LocalDateTime.now());
            resource.setUpdatedBy(member);
            resourceRepository.save(resource);
        } else {
            throw new AccessDeniedException();
        }
    }
}
