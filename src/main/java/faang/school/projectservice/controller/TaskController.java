package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TaskController {
    private final TaskService taskService;
    private final UserContext userContext;

    @PostMapping("/task")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@Valid @RequestBody TaskDto taskDto) {
        long userId = userContext.getUserId();
        return taskService.createTask(userId, taskDto);
    }

    @PutMapping("/task/{taskId}")
    public TaskDto updateTask(@Positive @PathVariable Long taskId,
                              @Valid @RequestBody UpdateTaskDto updateTaskDto) {
        long userId = userContext.getUserId();
        return taskService.updateTask(userId, taskId, updateTaskDto);
    }

    @GetMapping("/project/{projectId}/task")
    public List<TaskDto> getProjectTasks(@Positive @PathVariable Long projectId,
                                         @RequestBody TaskFilterDto filterDto) {
        long userId = userContext.getUserId();
        return taskService.getProjectTasks(userId, projectId, filterDto);
    }

    @GetMapping("/task/{taskId}")
    public TaskDto getTask(@Positive @PathVariable Long taskId) {
        long userId = userContext.getUserId();
        return taskService.getTask(userId, taskId);
    }
}
