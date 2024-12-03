package faang.school.projectservice.update.tasks;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.update.TaskUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParentTaskUpdate implements TaskUpdate {
    private final TaskRepository taskRepository;

    @Override
    public boolean isApplicable(TaskDto taskDto) {
        return taskDto.getParentTaskId() != null;
    }

    @Override
    public void apply(Task task, TaskDto taskDto) {
        task.setParentTask(taskRepository.getById(taskDto.getParentTaskId()));
    }
}
