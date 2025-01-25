package faang.school.projectservice.retriever.project.request;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.MeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectMeetsRetriever implements ProjectRetriever {
    private final MeetService meetService;

    @Override
    public void retrieveData(Project project, ProjectRequestDto projectRequestDto) {
        project.setMeets(meetService.getMeetsByIds(projectRequestDto.getMeetsIds()));
    }
}
