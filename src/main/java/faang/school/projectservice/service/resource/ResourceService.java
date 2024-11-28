package faang.school.projectservice.service.resource;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Resource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResourceService {

    private ResourceRepository resourceRepository;

    public Resource getResource(long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow(
                () -> new EntityNotFoundException("Resource", resourceId));
    }

    public void save(Resource resource) {
        resourceRepository.save(resource);
    }
}
