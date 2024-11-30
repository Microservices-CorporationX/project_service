package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.task.TaskDTO;
import faang.school.projectservice.dto.task.TaskFilterDTO;
import faang.school.projectservice.service.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/project/{projectId}")
    public TaskDTO createTask(@PathVariable Long projectId,@Valid @RequestBody TaskDTO taskDTO) {
        log.info("Получен запрос на создание задачи: {}", taskDTO);
        return taskService.createTask(taskDTO, projectId);
    }

    @PatchMapping("/project/{projectId}/tasks/{taskId}")
    public TaskDTO updateTask(@PathVariable Long projectId, @PathVariable Long taskId, @Valid @RequestBody TaskDTO taskDTO) {
        log.info("Получен запрос на обновление задачи с ID: {}", taskId);
        return taskService.updateTask(taskId, taskDTO, projectId);
    }

    @GetMapping("/project/{projectId}/tasks/{taskId}")
    public TaskDTO getTaskById(@PathVariable Long projectId, @PathVariable Long taskId) {
        log.info("Получен запрос на получение задачи с ID: {}", taskId);
        return taskService.getTaskById(taskId, projectId);
    }

    @PostMapping("/project/{projectId}/filter")
    public List<TaskDTO> getFilteredTasks(@PathVariable Long projectId,@Valid @RequestBody TaskFilterDTO taskFilterDTO) {
        log.info("Получен запрос на фильтрацию задач с фильтром: {}", taskFilterDTO);
        return taskService.getFilteredTasks(taskFilterDTO, projectId);
    }
}
