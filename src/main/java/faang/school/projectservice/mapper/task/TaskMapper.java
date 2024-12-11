package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    CreateTaskDto toCreateTaskDto(Task task);

    Task toEntity(CreateTaskDto dto);

    UpdateTaskDto toUpdateTaskDto(Task task);

    Task toEntity(UpdateTaskDto dto);
}