package faang.school.projectservice.retriever.project.request;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectStagesRetriever implements ProjectRetriever {
    private final StageService stageService;

    @Override
    public void retrieveData(Project project, ProjectRequestDto projectRequestDto) {
        project.setStages(stageService.getStagesByIds(projectRequestDto.getStagesIds()));
    }
}
