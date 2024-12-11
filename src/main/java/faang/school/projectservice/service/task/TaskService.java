package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.jpa.TaskJpaRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.update.TaskUpdate;
import faang.school.projectservice.validator.task.TaskUserVerification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final List<TaskUpdate> taskUpdates;

    public TaskDto createTask(Long userId, TaskDto taskDto) {
        log.info("Create task:{}", taskDto.toString());
        Task task = taskMapper.toTask(taskDto);
        taskUpdates.stream()
                .filter(taskUpdate -> taskUpdate.isApplicable(taskDto))
                .forEach(taskUpdate -> taskUpdate.apply(task, taskDto));
        log.info(task.toString());
        return taskMapper.toTaskDto(taskRepository.create(userId, task));
    }

    public TaskDto updateTask(Long userId, TaskDto taskDto) {
        Task task = taskRepository.getById(taskDto.getId());
        TaskUserVerification.userVerification(userId, task);

        taskUpdates.stream()
                .filter(taskUpdate -> taskUpdate.isApplicable(taskDto))
                .forEach(taskUpdate -> taskUpdate.apply(task, taskDto));

        return taskMapper.toTaskDto(taskRepository.update(userId, task));
    }

    public TaskDto getTaskById(Long taskId) {
        return taskMapper.toTaskDto(taskRepository.getById(taskId));
    }

    public List<TaskDto> getAllTasks() {
        return taskMapper.toTaskDtos(taskRepository.findAll());
    }

    public void saveAll(List<Task> taskList) {
        taskRepository.saveAll(taskList);
    }

}
