package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "status", constant = "TODO")
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "parentTask", ignore = true)
    @Mapping(target = "stage", ignore = true)
    Task toEntity(TaskDto taskDto);

    @Mapping(target = "parentTaskId", source = "parentTask.id")
    @Mapping(target = "stageId", source = "stage.stageId")
    @Mapping(target = "projectId", source = "project.id")
    TaskDto toDto(Task task);

    @Mapping(target = "parentTaskId", source = "parentTask.id")
    @Mapping(target = "stageId", source = "stage.id")
    @Mapping(target = "projectId", source = "project.id")
    List<TaskDto> toDto(List<Task> task);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UpdateTaskDto updateTaskDto, @MappingTarget Task task);
}
