package faang.school.projectservice.retriever.project.request;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectResourcesRetriever implements ProjectRetriever {
    private final ResourceService resourceService;

    @Override
    public void retrieveData(Project project, ProjectRequestDto projectRequestDto) {
        project.setResources(resourceService.getResourcesByIds(projectRequestDto.getResourcesIds()));
    }
}
