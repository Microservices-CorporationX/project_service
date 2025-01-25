package faang.school.projectservice.retriever.project.request;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectParentRetriever implements ProjectRetriever {
    private final ProjectService projectService;

    @Override
    public void retrieveData(Project project, ProjectRequestDto projectRequestDto) {
        project.setParentProject(projectService.getProjectById(projectRequestDto.getParentProjectId()));
    }
}
