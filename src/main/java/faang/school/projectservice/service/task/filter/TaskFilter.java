package faang.school.projectservice.service.task.filter;

import faang.school.projectservice.dto.task.TaskFilterDTO;
import faang.school.projectservice.model.Task;

import java.util.stream.Stream;

public interface TaskFilter {
    boolean isApplicable(TaskFilterDTO filterDTO);

    Stream<Task> apply(Stream<Task> taskStream, TaskFilterDTO filterDTO);
}
