package faang.school.projectservice.update.tasks;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.update.TaskUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkedTaskUpdate implements TaskUpdate {
    private final TaskRepository taskRepository;

    @Override
    public boolean isApplicable(TaskDto taskDto) {
        return taskDto.getLinkedTaskIds() != null;
    }

    @Override
    public void apply(Task task, TaskDto taskDto) {
        task.setLinkedTasks(
                taskDto.getLinkedTaskIds().stream()
                        .map(taskRepository::getById) // Преобразование ID в сущности Task
                        .toList()
        );
    }
}
