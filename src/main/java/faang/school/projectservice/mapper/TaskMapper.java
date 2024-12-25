package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.ResponseTaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    Task toEntity(CreateTaskDto dto);

    @Mapping(target = "projectId", expression = "java(task.getProject().getId())")
    @Mapping(target = "stageId", expression = "java(task.getStage().getStageId())")
    @Mapping(target = "parentTaskId", expression = "java(getParentTaskId(task))")
    ResponseTaskDto toDto(Task task);

    default Long getParentTaskId(Task task) {
        if (task.getParentTask() != null) {
            return task.getParentTask().getId();
        }
        return null;
    }
}
