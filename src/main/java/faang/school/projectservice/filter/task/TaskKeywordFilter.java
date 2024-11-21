package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskKeywordFilter implements TaskFilter {

    @Override
    public boolean isApplicable(TaskFilterDto filterDto) {
        return filterDto.keyword() != null;
    }

    @Override
    public List<Task> apply(List<Task> tasks, TaskFilterDto filterDto) {
        return tasks.stream()
                .filter(task -> task.getDescription() != null)
                .filter(task -> task.getDescription().contains(filterDto.keyword()))
                .toList();
    }
}
