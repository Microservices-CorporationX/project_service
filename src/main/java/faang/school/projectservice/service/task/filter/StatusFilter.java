package faang.school.projectservice.service.task.filter;

import faang.school.projectservice.dto.task.TaskFilterDTO;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StatusFilter implements TaskFilter {
    @Override
    public boolean isApplicable(TaskFilterDTO filterDTO) {
        return filterDTO.getStatus() != null;
    }

    @Override
    public Stream<Task> apply(Stream<Task> taskStream, TaskFilterDTO filterDTO) {
        TaskStatus status = TaskStatus.valueOf(filterDTO.getStatus().toUpperCase());
        return taskStream.filter(task -> task.getStatus() == status);
    }
}
