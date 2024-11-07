package faang.school.projectservice.update.project;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.update.ProjectUpdate;
import org.springframework.stereotype.Component;

@Component
public class DescriptionUpdate implements ProjectUpdate {
    @Override
    public boolean isApplicable(ProjectDto projectDto) {
        return projectDto.getDescription() != null;
    }

    @Override
    public void apply(ProjectDto projectDto, Project project) {
        project.setDescription(projectDto.getDescription());
    }
}
