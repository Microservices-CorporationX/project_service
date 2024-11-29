package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.TaskDTO;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stage.id", target = "stageId")
    @Mapping(source = "performerUser.id", target = "performerUserId")
    @Mapping(source = "reporterUser.id", target = "reporterUserId")
    Task toEntity(TaskDTO taskDto);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "stageId", target = "stage.id")
    @Mapping(source = "performerUserId", target = "performerUser.id")
    @Mapping(source = "reporterUserId", target = "reporterUser.id")
    TaskDTO toDto(Task task);
}
