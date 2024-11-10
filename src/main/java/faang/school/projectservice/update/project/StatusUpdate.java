package faang.school.projectservice.update.project;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.update.ProjectUpdate;
import org.springframework.stereotype.Component;

@Component
public class StatusUpdate implements ProjectUpdate {
    @Override
    public boolean isApplicable(ProjectDto projectDto) {
        return projectDto.getStatus() != null;
    }

    @Override
    public void apply(ProjectDto projectDto, Project project) {
        project.setStatus(projectDto.getStatus());
    }
}
