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
public class OnHoldStatusUpdate implements StatusUpdater {
    private final ProjectValidator projectValidator;
    private final ProjectRepository projectRepository;

    @Override
    public boolean isApplicable(UpdateSubProjectDto updateSubProjectDto) {
        return updateSubProjectDto.getStatus() == ProjectStatus.ON_HOLD;
    }

    @Override
    public void changeStatus(Project project) {
        projectValidator.validateProjectStatusValidToHold(project);
        project.setStatus(ProjectStatus.ON_HOLD);
        if (projectValidator.hasChildrenProjects(project)) {
            project.getChildren().forEach(this::changeStatus);
        }
        projectRepository.save(project);
    }
}
