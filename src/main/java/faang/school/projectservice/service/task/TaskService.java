package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.jpa.TaskJpaRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.validator.task.TaskUserVerification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskDto createTask(Long userId, TaskDto taskDto) {
        Task task = taskMapper.toTask(taskDto);

        return taskMapper.toTaskDto(taskRepository.create(userId, task));
    }

    public TaskDto updateTask(Long userId, TaskDto taskDto) {
        Task task = taskRepository.getById(taskDto.getId());
        TaskUserVerification.userVerification(userId, task);

        return taskMapper.toTaskDto(taskRepository.update(userId, task));
    }



    public void saveAll(List<Task> taskList) {
        taskRepository.saveAll(taskList);
    }

}
