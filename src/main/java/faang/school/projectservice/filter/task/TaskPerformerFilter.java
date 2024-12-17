package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TaskPerformerFilter implements Filter<Task, TaskFilterDto> {

    @Override
    public boolean isApplicable(TaskFilterDto filterDto) {
        return filterDto != null &&
                filterDto.getPerformerUserId() != null;
    }

    @Override
    public Stream<Task> apply(Stream<Task> tasks, TaskFilterDto filterDto) {
        return tasks.filter(task ->
                task.getPerformerUserId().equals(filterDto.getPerformerUserId()));
    }
}
