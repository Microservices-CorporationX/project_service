package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.task.TaskDTO;
import faang.school.projectservice.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
@Validated
@Slf4j(topic = "TaskController")
public class TaskController {

    private TaskService taskService;

    @PostMapping
    public void createTask(@Validated @RequestBody TaskDTO taskDTO) {
        log.info("Получен запрос на обновление задачи с названием: {}", taskDTO.getName());\
        return taskService.updateTask(taskDTO);
    }

    @PatchMapping("/{taskId}")
    public void updateTask(@PathVariable("taskId")Long taskId,
                           @Validated @RequestBody TaskDTO taskDTO) {
        log.info("Получен запрос на обновление задачи с ID: {}", taskId);
        return taskService.updateTask(taskId, taskDTO);
    }

    @GetMapping("/project/{projectId}")
    public List<TaskDTO> getTasksByProjectId(@PathVariable("projectId")Long projectId,
                                             @RequestParam(required = false)TaskStatus status,
                                             @RequestParam(required = false) Long performerUserId,
                                             @RequestParam(required = false) String keyword) {
        log.info("Получен запрос на получение задач по проекту с ID: {}", projectId);
        return taskService.getTasksByProjectId(projectId, status, performerUserId, keyword);
    }

    @GetMapping("/{taskId}")
    public TaskDTO getTaskById(@PathVariable("taskId")Long taskId) {
        log.info("Получен запрос на получение задачи с ID: {}", taskId);
        return taskService.getTaskById(taskId);
    }
}
