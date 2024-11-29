package faang.school.projectservice.service.task.filter;

import faang.school.projectservice.dto.task.TaskFilterDTO;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class StatusFilter implements TaskFilter {
    @Override
    public boolean isApplicable(TaskFilterDTO filterDTO) {
        return filterDTO.getStatus() != null;
    }

    @Override
    public Stream<Task> apply(Stream<Task> taskStream, TaskFilterDTO filterDTO) {
        return taskStream.filter(task -> Objects.equals(task.getStatus(), filterDTO.getStatus()));
    }
}
