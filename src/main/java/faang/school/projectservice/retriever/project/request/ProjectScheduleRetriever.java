package faang.school.projectservice.retriever.project.request;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectScheduleRetriever implements ProjectRetriever {
    private final ScheduleService scheduleService;

    @Override
    public void retrieveData(Project project, ProjectRequestDto projectRequestDto) {
        project.setSchedule(scheduleService.getScheduleById(projectRequestDto.getScheduleId()));
    }
}
