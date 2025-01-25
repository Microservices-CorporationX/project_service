package faang.school.projectservice.retriever.project.request;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectTasksRetriever implements ProjectRetriever {
    private final TaskService taskService;

    @Override
    public void retrieveData(Project project, ProjectRequestDto projectRequestDto) {
        project.setTasks(taskService.findTasksByIds(projectRequestDto.getTasksIds()));
    }
}
