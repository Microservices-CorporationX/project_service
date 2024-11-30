package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.mapper.stage.StageRolesMapper;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = StageRolesMapper.class)
public interface TaskMapper {

    @Mapping(source = "task", target = "taskDto")
    TaskDto toTaskDto(Task task);

    @Mapping(source = "taskDto", target = "task")
    Task toTask(TaskDto taskDto);

    @Mapping(source = "tasks", target = "taskDtos")
    List<TaskDto> toTaskDtos(List<Task> tasks);

    @Mapping(source = "taskDtos", target = "tasks")
    List<Task> toTasks(List<TaskDto> taskDtos);
}
