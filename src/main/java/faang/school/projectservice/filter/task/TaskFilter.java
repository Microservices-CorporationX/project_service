package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;

import java.util.List;

public interface TaskFilter {

    boolean isApplicable(TaskFilterDto filterDto);

    List<Task> apply(List<Task> tasks, TaskFilterDto filterDto);
}
