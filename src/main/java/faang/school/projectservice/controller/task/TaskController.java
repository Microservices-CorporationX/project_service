package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.task.TaskDTO;
import faang.school.projectservice.dto.task.TaskFilterDTO;
import faang.school.projectservice.service.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO createTask(@Valid @RequestBody TaskDTO taskDTO) {
        log.info("Получен запрос на создание задачи: {}", taskDTO);
        return taskService.createTask(taskDTO);
    }

    @PatchMapping("/{taskId}")
    public TaskDTO updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskDTO taskDTO) {
        log.info("Получен запрос на обновление задачи с ID: {}", taskId);
        return taskService.updateTask(taskId, taskDTO);
    }

    @GetMapping("/{taskId}")
    public TaskDTO getTaskById(@PathVariable Long taskId) {
        log.info("Получен запрос на получение задачи с ID: {}", taskId);
        return taskService.getTaskById(taskId);
    }

    @PostMapping("/filter")
    public List<TaskDTO> getFilteredTasks(@Valid @RequestBody TaskFilterDTO taskFilterDTO) {
        log.info("Получен запрос на фильтрацию задач с фильтром: {}", taskFilterDTO);
        return taskService.getFilteredTasks(taskFilterDTO);
    }

    @GetMapping("/project/{projectId}")
    public List<TaskDTO> getAllTasksByProjectId(@PathVariable Long projectId) {
        log.info("Получен запрос на получение всех задач для проекта с ID: {}", projectId);
        return taskService.getAllTasksByProjectId(projectId);
    }
}
