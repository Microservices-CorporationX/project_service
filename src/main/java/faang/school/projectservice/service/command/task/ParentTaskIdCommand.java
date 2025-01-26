package faang.school.projectservice.service.command.task;

import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.exception.TaskWasNotFoundException;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.command.TaskUpdateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParentTaskIdCommand implements TaskUpdateCommand {
    private final TaskRepository taskRepository;

    @Override
    public boolean isApplicable(UpdateTaskDto dto) {
        return dto.parentTaskId() != null;
    }

    @Override
    public void execute(Task task, UpdateTaskDto dto) {
        Task parent = findTaskById(dto.parentTaskId());
        task.setParentTask(parent);
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("task with was not found -> id : " + taskId));
    }
}
