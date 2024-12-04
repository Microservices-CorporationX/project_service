package faang.school.projectservice.service.task.filter;

import faang.school.projectservice.dto.task.TaskFilterDTO;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class KeywordFilter implements TaskFilter {
    @Override
    public boolean isApplicable(TaskFilterDTO filterDTO) {
        return filterDTO.getKeyword() != null && !filterDTO.getKeyword().isEmpty();
    }

    @Override
    public Stream<Task> apply(Stream<Task> taskStream, TaskFilterDTO filterDTO) {
        return taskStream.filter(task ->
            task.getName().contains(filterDTO.getKeyword()) || task.getDescription().contains(filterDTO.getKeyword())
        );
    }
}
