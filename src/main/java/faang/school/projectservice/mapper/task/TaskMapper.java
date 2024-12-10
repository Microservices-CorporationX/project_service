package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

@Component
public interface TaskMapper {

   CreateTaskDto toDto (Task task);

   Task toEntity (CreateTaskDto dto);
}
