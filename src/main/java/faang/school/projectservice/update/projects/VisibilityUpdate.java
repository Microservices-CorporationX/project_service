package faang.school.projectservice.update.projects;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.update.ProjectUpdate;
import faang.school.projectservice.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public class VisibilityUpdate implements ProjectUpdate {
    @Override
    public boolean isApplicable(ProjectDto projectDto) {
        return projectDto.getVisibility() != null;
    }

    @Override
    public void apply(Project project, ProjectDto projectDto) {
        if (project.getParentProject().getVisibility() != null) {
            Validator.checkVisibility(project.getParentProject().getVisibility(), projectDto.getVisibility());
        }

        project.setVisibility(projectDto.getVisibility());

        if (projectDto.getVisibility() == ProjectVisibility.PRIVATE) {
            project.getChildren().forEach(subProject ->
                    subProject.setVisibility(ProjectVisibility.PRIVATE)
            );
        }
    }
}

