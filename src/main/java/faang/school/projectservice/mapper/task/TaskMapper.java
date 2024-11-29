package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.TaskDTO;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    Task toEntity(TaskDTO taskDTO);

    TaskDTO toDTO(Task task);
}
