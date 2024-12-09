package faang.school.projectservice.update.projects;

import faang.school.projectservice.dto.project.ProjectDto;
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
    public void apply(Project project, ProjectDto projectDto) {
        project.setStatus(projectDto.getStatus());
    }
}

