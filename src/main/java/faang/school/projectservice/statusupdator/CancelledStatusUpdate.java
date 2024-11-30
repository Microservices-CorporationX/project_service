package faang.school.projectservice.statusupdator;

import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelledStatusUpdate implements StatusUpdater {
    private final ProjectValidator projectValidator;
    private final ProjectRepository projectRepository;

    @Override
    public boolean isApplicable(UpdateSubProjectDto updateSubProjectDto) {
        return updateSubProjectDto.getStatus() == ProjectStatus.CANCELLED;
    }

    @Override
    public void changeStatus(Project project) {
        project.setStatus(ProjectStatus.CANCELLED);
        if (projectValidator.hasChildrenProjects(project)) {
            project.getChildren().forEach(this::changeStatus);
        }
        projectRepository.save(project);
    }
}
