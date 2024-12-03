package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.service.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("{userId}")
    public TaskDto createTask(@PathVariable Long userId, @Valid @RequestBody TaskDto taskDto) {
        return taskService.createTask(userId, taskDto);
    }

    @PutMapping("{userId}")
    public TaskDto updateTask(@PathVariable Long userId, @Valid @RequestBody TaskDto taskDto) {
        return taskService.updateTask(userId, taskDto);
    }
}
