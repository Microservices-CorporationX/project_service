package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFiltersDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class TaskStatusFilter implements Filter<Task, TaskFiltersDto> {
    @Override
    public boolean isApplicable(TaskFiltersDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<Task> apply(Stream<Task> tasks, TaskFiltersDto filters) {
        return tasks.filter(task -> Objects.equals(task.getStatus(), filters.getStatus()));
    }
}
