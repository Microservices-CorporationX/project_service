package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.TaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "minutesTracked", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parentTask", ignore = true)
    @Mapping(target = "linkedTasks", ignore = true)
    @Mapping(target = "stage", ignore = true)
    Task toEntity(TaskDto taskDto);

    @Mapping(source = "project.id", target = "projectId")
    TaskDto toDto(Task task);
}
