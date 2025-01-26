package faang.school.projectservice.service.command.task;

import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.service.command.TaskUpdateCommand;
import org.springframework.stereotype.Component;

@Component
public class PerformerUserIdCommand implements TaskUpdateCommand {
    @Override
    public boolean isApplicable(UpdateTaskDto dto) {
        return dto.performerUserId() != null;
    }

    @Override
    public void execute(Task task, UpdateTaskDto dto) {
        task.setPerformerUserId(dto.performerUserId());
    }
}
