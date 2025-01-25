package faang.school.projectservice.retriever.project.request;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectMomentsRetriever implements ProjectRetriever {
    private final MomentService momentService;

    @Override
    public void retrieveData(Project project, ProjectRequestDto projectRequestDto) {
        project.setMoments(momentService.getMomentsByIds(projectRequestDto.getMomentsIds()));
    }
}
