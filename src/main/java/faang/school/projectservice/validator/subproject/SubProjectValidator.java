package faang.school.projectservice.validator.subproject;

import com.amazonaws.services.kms.model.AlreadyExistsException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubProjectValidator {

    private final ProjectRepository projectRepository;
    private final RestTemplate restTemplate;

    @Value("${services.user-service.host}")
    private String userServiceHost;

    @Value("${services.user-service.port}")
    private String userServicePort;

    public void validateOwnerExistence(Long ownerId) {
        if (getNotExistingUserIds(List.of(ownerId)).isEmpty()) {
            log.info("User with id = '{}' doesn't exist", ownerId);
            throw new EntityNotFoundException("User with id = " + ownerId + " doesn't exist");
        }
    }

    public void validateSubProjectVisibility(ProjectVisibility parentStatus, ProjectVisibility childStatus) {
        if (parentStatus == ProjectVisibility.PRIVATE &&
                childStatus == ProjectVisibility.PUBLIC) {
            log.info("Cannot create public sub project for private parent project");
            throw new IllegalArgumentException("Cannot create public sub project for private parent project");
        }
    }

    public void validateExistenceByOwnerIdAndName(Long ownerId, String name) {
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            log.info("User with id = '{}' already has project with name = '{}'", ownerId, name);
            throw new AlreadyExistsException("User already has project with this name");
        }
    }

    public void validateOwnership(Long userId, Long projectId) {
        if (!projectId.equals(userId)) {
            log.info("User with id = '{}' is not owner of project with id = '{}'", userId, projectId);
            throw new IllegalArgumentException("Owner id mismatch");
        }
    }

    public void validateSubProjectBelonging(Long parentId, Project subProject) {
        if (!parentId.equals(subProject.getParentProject().getId())) {
            log.info("Project with id = '{}' does not belong to parent project", subProject.getId());
            throw new IllegalArgumentException("Sub project does not belong to parent project");
        }
    }

    public void validateSubProjectStatus(Project subProject, ProjectStatus newStatus) {
        if (newStatus == ProjectStatus.CANCELLED || newStatus == ProjectStatus.COMPLETED) {
            boolean isAnyChildActive = subProject.getChildren().stream()
                    .anyMatch(child ->
                            !child.getStatus().equals(ProjectStatus.CANCELLED) &&
                                    !child.getStatus().equals(ProjectStatus.COMPLETED));
            if (isAnyChildActive) {
                log.info("Cannot change status for sub project with id = '{}'", subProject.getId());
                throw new IllegalArgumentException("Cannot change status for sub project");
            }
        }
    }

    private List<Long> getNotExistingUserIds(List<Long> userIds) {
        String uri = userServiceHost + ":" + userServicePort + "/users/not-existing-ids";

        RequestEntity<List<Long>> request = RequestEntity
                .post(URI.create(uri))
                .body(userIds);

        try {
            ResponseEntity<List<Long>> response = restTemplate.exchange(
                    request,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (RestClientException e) {
            log.error("An error occurred when requesting an external User Service!", e);
            throw new RestClientException("An error occurred when requesting an external User Service!", e);
        }
    }
}
