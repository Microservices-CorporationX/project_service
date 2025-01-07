package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.update.TaskUpdate;
import faang.school.projectservice.validator.task.TaskUserVerification;
import jakarta.transaction.Transactional;
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

    @Transactional
    public TaskDto createTask(Long userId, TaskDto taskDto) {
        log.info("Create task: {}", taskDto.toString());

        Task task = taskRepository.create(
                taskDto.getName(),
                taskDto.getDescription(),
                taskDto.getStatus().name(),
                userId,
                taskDto.getParentTaskId(),
                taskDto.getProjectId(),
                taskDto.getStageId()
        );
        if (taskDto.getLinkedTaskIds() != null) {
            taskDto.getLinkedTaskIds().forEach(linkedTaskId ->
                taskRepository.linkTask(task.getId(), linkedTaskId)
            );
        }
        log.info("Task created: {}", task.toString());
        return taskMapper.toTaskDto(task);
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

//    public List<TaskDto> getAllTasks() {
//        return taskMapper.toTaskDtos(taskRepository.findAll());
//    }

    public void saveAll(List<Task> taskList) {
        taskRepository.saveAll(taskList);
    }

}
