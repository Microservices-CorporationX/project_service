package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ResourceService {
    void deleteResource(Long resourceId);
    ResourceResponseDto addResource(Long projectId, MultipartFile file);
    InputStream downloadResource(Long resourceId);
}
