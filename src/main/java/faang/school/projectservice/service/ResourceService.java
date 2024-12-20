package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ResourceService {

    ResourceDto addResource(Long projectId, MultipartFile file);
    String uploadProjectCover(Long projectId, MultipartFile file);
    InputStream downloadResource(Long resourceId);
    void deleteResource(long resourceId, long userId);
    ResourceDto updateResource(Long resourceId, Long userId, MultipartFile file);
}
