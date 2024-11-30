package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Task;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskDto createTask(TaskDto taskDto) {
        Task task = taskMapper.toTask(taskDto);

        return taskMapper.toTaskDto(taskRepository.save(task));
    }

    public TaskDto updateTask(@Valid @RequestBody TaskDto taskDto) {
        Task task = taskRepository.findById(taskDto.getId()).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                        "Task not found by id: %s", taskDto.getId()))
        );

        return
    }

    public void saveAll(List<Task> taskList) {
        taskRepository.saveAll(taskList);
    }

}
