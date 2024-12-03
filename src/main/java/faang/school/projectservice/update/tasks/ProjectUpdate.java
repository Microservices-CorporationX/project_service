package faang.school.projectservice.update.tasks;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.update.TaskUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectUpdate implements TaskUpdate {
    private final ProjectRepository projectRepository;

    @Override
    public boolean isApplicable(TaskDto taskDto) {
        return taskDto.getProjectId() != null;
    }

    @Override
    public void apply(Task task, TaskDto taskDto) {
        task.setProject(projectRepository.getProjectById(taskDto.getProjectId()));
    }
}
