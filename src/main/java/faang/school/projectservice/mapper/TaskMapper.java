package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.CreateTaskResult;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    Task toEntity(CreateTaskResult createTaskResult);

    CreateTaskResult toDto(Task task);
}
