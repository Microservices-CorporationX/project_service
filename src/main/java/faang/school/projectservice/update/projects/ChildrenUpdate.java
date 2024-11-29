package faang.school.projectservice.update.projects;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.update.ProjectUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChildrenUpdate implements ProjectUpdate {
    private final ProjectRepository projectRepository;

    @Override
    public boolean isApplicable(ProjectDto projectDto) {
        return projectDto.getChildrenIds() != null;
    }

    @Override
    public void apply(Project project, ProjectDto projectDto) {
        project.setChildren(
                projectDto.getChildrenIds().stream()
                        .map(projectRepository::getProjectById)
                        .toList()
        );
    }
}
