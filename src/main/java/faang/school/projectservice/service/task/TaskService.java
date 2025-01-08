package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
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

    @Transactional
    public TaskDto createTask(Long userId, TaskDto taskDto) {
        log.info("Create task: {}", taskDto.toString());

        Task task = taskRepository.save(
                taskDto.getName(),
                taskDto.getDescription(),
                taskDto.getStatus().name(),
                userId,
                taskDto.getReporterUserId(),
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

    @Transactional
    public TaskDto updateTask(Long userId, TaskDto taskDto) {
        log.info("Update task: {}", taskDto.toString());
        Task task = taskRepository.getById(taskDto.getId());
        TaskUserVerification.userVerification(userId, task);

        taskRepository.update(
                task.getId(),
                taskDto.getDescription(),
                taskDto.getStatus().name(),
                taskDto.getMinutesTracked(),
                userId,
                taskDto.getParentTaskId()
        );
        if (taskDto.getLinkedTaskIds() != null) {
            taskRepository.unlinkTask(task.getId());
            taskDto.getLinkedTaskIds().forEach(linkedTaskId ->
                    taskRepository.linkTask(task.getId(), linkedTaskId)
            );
        }
        log.info("Task updated: {}", task.toString());
        return taskMapper.toTaskDto(task);
    }

    public TaskDto getTaskById(Long userId, Long taskId) {
        //TaskUserVerification.userVerification(userId, task);
        return taskMapper.toTaskDto(taskRepository.getById(taskId));
    }

    public List<TaskDto> getTasksByProject(Long userId, Long projectId) {
        //TaskUserVerification.userVerification(userId, task);
        return taskMapper.toTaskDtos(taskRepository.findAllByProjectId(projectId));
    }

    public void saveAll(List<Task> taskList) {
        taskRepository.saveAll(taskList);
    }

}
