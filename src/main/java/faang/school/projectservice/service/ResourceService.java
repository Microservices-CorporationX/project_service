package faang.school.projectservice.service;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public List<Resource> getResourcesByIds(List<Long> resourceIds) {
        return resourceRepository.findAllById(resourceIds);
    }
}
