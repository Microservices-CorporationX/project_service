package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFiltersDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class TaskPerformerUserIdFilter implements Filter<Task, TaskFiltersDto> {
    @Override
    public boolean isApplicable(TaskFiltersDto filters) {
        return filters.getPerformerUserId() != null;
    }

    @Override
    public Stream<Task> apply(Stream<Task> tasks, TaskFiltersDto filters) {
        return tasks.filter(task -> Objects.equals(task.getPerformerUserId(), filters.getPerformerUserId()));
    }
}
