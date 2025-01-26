package faang.school.projectservice.service.command.task;

import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.command.TaskUpdateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateLinkedTasksCommand implements TaskUpdateCommand {
    private final TaskRepository taskRepository;

    @Override
    public boolean isApplicable(UpdateTaskDto dto) {
        return dto.linkedTaskIds() != null && !dto.linkedTaskIds().isEmpty();
    }

    @Override
    public void execute(Task task, UpdateTaskDto dto) {
        List<Task> linkedTasks = dto.linkedTaskIds().stream()
                .map(this::findTaskById)
                .toList();

        task.setLinkedTasks(linkedTasks);
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("task with was not found -> id : " + taskId));
    }
}
