package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.getProjectStatus() != null;
    }

    @Override
    public void apply(List<Project> projectStream, ProjectFilterDto projectFilterDto) {
        projectStream.removeIf(project -> !(project.getStatus() == projectFilterDto.getProjectStatus()));
    }
}
