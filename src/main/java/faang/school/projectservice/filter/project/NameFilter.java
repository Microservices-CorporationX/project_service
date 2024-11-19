package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.getName() != null;
    }

    @Override
    public void apply(List<Project> projectStream, ProjectFilterDto projectFilterDto) {
        projectStream.removeIf(project -> !project.getName().contains(projectFilterDto.getName()));
    }
}
