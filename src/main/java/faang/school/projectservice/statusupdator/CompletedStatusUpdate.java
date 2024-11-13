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
public class CompletedStatusUpdate implements StatusUpdater {
    private final ProjectValidator projectValidator;
    private final ProjectRepository projectRepository;

    @Override
    public boolean isApplicable(UpdateSubProjectDto updateSubProjectDto) {
        return updateSubProjectDto.getStatus() == ProjectStatus.COMPLETED;
    }

    @Override
    public void changeStatus(Project project) {
        projectValidator.validateProjectIsValidToComplete(project);
        project.setStatus(ProjectStatus.COMPLETED);
        projectRepository.save(project);
    }
}
