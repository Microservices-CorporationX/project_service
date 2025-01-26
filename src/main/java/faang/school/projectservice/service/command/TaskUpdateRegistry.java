package faang.school.projectservice.service.command;

import faang.school.projectservice.dto.task.UpdateTaskDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskUpdateRegistry {
    private final List<TaskUpdateCommand> commands;

    public List<TaskUpdateCommand> getCommands(UpdateTaskDto updateTaskDto) {
        return commands.stream()
                .filter(command -> command.isApplicable(updateTaskDto))
                .toList();
    }
}
