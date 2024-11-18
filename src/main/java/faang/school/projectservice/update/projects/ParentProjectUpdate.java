package faang.school.projectservice.update.projects;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.update.ProjectUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParentProjectUpdate implements ProjectUpdate {
    private final ProjectRepository projectRepository;

    @Override
    public boolean isApplicable(ProjectDto projectDto) {
        return projectDto.getParentProjectId() != null;
    }

    @Override
    public void apply(Project project, ProjectDto projectDto) {
        project.setParentProject(projectRepository.getProjectById(projectDto.getParentProjectId()));
    }
}
