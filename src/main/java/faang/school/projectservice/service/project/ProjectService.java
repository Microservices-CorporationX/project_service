package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.stage.StageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private ProjectRepository projectRepository;
    private StageValidator stageValidator;

    public Project getProjectById(Long projectId) {
        log.info("Retrieving project by ID: {}", projectId);

        stageValidator.validationOnNull(projectId, "Project ID cannot be null");

        Project project = projectRepository.getProjectById(projectId);

        stageValidator.validationOnNull(project, "Project not found for ID: " + projectId);

        log.info("Retrieved project: {}", project);
        return project;
    }
}
