package faang.school.projectservice.service.command;

import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.model.Task;

public interface TaskUpdateCommand {
    boolean isApplicable(UpdateTaskDto dto);

    void execute(Task task, UpdateTaskDto dto);
}
