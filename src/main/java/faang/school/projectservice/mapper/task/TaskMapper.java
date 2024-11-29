package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.TaskDTO;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    Task toEntity(TaskDTO taskDto);

    TaskDTO toDto(Task task);
}
