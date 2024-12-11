package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.service.task.TaskService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping()
    public void createTask(@RequestBody @Validated CreateTaskDto taskDto,
                           @RequestHeader("x-team-member-id") long creatorId) {
        taskService.createTask(taskDto, creatorId);
    }

    @PutMapping()
    public void updateTask(@RequestBody @Validated UpdateTaskDto taskDto,
                           @RequestHeader("x-team-member-id") long updaterId) {
        taskService.updateTask(taskDto, updaterId);
    }

    @PostMapping("/filters")
    public List<TaskDto> getAllTasks(@RequestBody TaskFilterDto taskFilterDto,
                                     @RequestParam Long projectId,
                                     @RequestHeader("x-team-member-id") long requesterId) {
        return taskService.getAllTasks(taskFilterDto, requesterId, projectId);
    }

    @GetMapping("/{taskId}")
    public TaskDto getTask(@PathVariable long taskId,
                           @RequestHeader("x-team-member-id") long requesterId) {
        return taskService.getTask(taskId, requesterId);
    }
}
