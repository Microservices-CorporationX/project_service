package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Task;

import java.util.stream.Stream;

public class TaskDescriptionFilter implements Filter<Task, TaskFilterDto> {

    @Override
    public boolean isApplicable(TaskFilterDto filterDto) {
        return filterDto != null &&
                filterDto.getDescriptionPattern() != null &&
                !filterDto.getDescriptionPattern().isBlank();
    }

    @Override
    public Stream<Task> apply(Stream<Task> tasks, TaskFilterDto filterDto) {
        return tasks.filter(task ->
                task.getDescription().toLowerCase().
                        contains(filterDto.getDescriptionPattern().toLowerCase()));
    }
}
