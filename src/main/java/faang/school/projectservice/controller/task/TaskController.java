package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.service.task.TaskService;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping()
    public void createTask(@RequestBody CreateTaskDto taskDto,
                           @RequestHeader("x-team-member-id") long creatorId) {
        taskService.createTask(taskDto,creatorId);
    }

    @PutMapping()
    public void updateTask(@RequestBody TaskDto taskDto) {
        taskService.updateTask(taskDto);
    }

    @PostMapping("/filters")
    public List<TaskDto> getAllTasks(@RequestBody TaskFilterDto taskFilterDto) {
        return taskService.getAllTasks(taskFilterDto);
    }

    @GetMapping("/{taskId}")
    public TaskDto getTask(@PathVariable long taskId) {
        return taskService.getAllTasks(taskFilterDto);
    }
}
