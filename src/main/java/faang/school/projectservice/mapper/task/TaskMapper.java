package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(source = "",target = "")
    TaskDto toTaskDto(Task task);

    Task toEntity(CreateTaskDto dto);

    Task toEntity(UpdateTaskDto dto);
}