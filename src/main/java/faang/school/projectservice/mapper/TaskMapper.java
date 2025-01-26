package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.TaskResult;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    Task toEntity(TaskResult createTaskResult);

    TaskResult toDto(Task task);
}
