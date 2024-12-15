package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.task.CreateUpdateTaskDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.service.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController()
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping()
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid CreateUpdateTaskDto taskDto,
                                              @RequestHeader("x-team-member-id") long creatorId) {
        TaskDto resultTaskDto = taskService.createTask(taskDto, creatorId);
        return ResponseEntity.ok(resultTaskDto);
    }

    @PutMapping()
    public ResponseEntity<TaskDto> updateTask(@RequestBody @Valid CreateUpdateTaskDto taskDto,
                                              @RequestHeader("x-team-member-id") long updaterId) {
        TaskDto resultTaskDto = taskService.updateTask(taskDto, updaterId);
        return ResponseEntity.ok(resultTaskDto);
    }

    @PostMapping("/filters")
    public ResponseEntity<List<TaskDto>> getAllTasks(@RequestBody TaskFilterDto taskFilterDto,
                                                     @RequestParam Long projectId,
                                                     @RequestHeader("x-team-member-id") long requesterId) {
        List<TaskDto> resultTaskDto = taskService.getAllTasks(taskFilterDto, projectId, requesterId);
        return ResponseEntity.ok(resultTaskDto);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTask(@PathVariable long taskId,
                                           @RequestHeader("x-team-member-id") long requesterId) {
        TaskDto resultTaskDto = taskService.getTask(taskId, requesterId);
        return ResponseEntity.ok(resultTaskDto);
    }
}