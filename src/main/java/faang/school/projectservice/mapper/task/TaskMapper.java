package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.TaskDTO;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "projectId", source = "project.id")
    TaskDTO toDto(Task task);

    @Mapping(target = "project.id", source = "projectId")
    Task toEntity(TaskDTO taskDto);

    @Mapping(target = "id", ignore = true)
    void updateTaskFromDto(TaskDTO taskDTO, @MappingTarget Task task);
}
