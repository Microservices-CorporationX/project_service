package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.TaskResult;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTaskFromDto(UpdateTaskDto dto, @MappingTarget Task task);

    TaskResult toDto(Task task);
}
