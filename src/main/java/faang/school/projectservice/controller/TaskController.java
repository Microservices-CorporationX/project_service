package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.docs.task.CreateTaskDoc;
import faang.school.projectservice.docs.task.FiltersTaskDoc;
import faang.school.projectservice.docs.task.GetTaskByIdDoc;
import faang.school.projectservice.docs.task.GetTasksDoc;
import faang.school.projectservice.docs.task.UpdateTaskDoc;
import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.ResponseTaskDto;
import faang.school.projectservice.dto.task.TaskFiltersDto;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@Validated
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Task", description = "This class is used to manage tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserContext userContext;

    @PostMapping
    @CreateTaskDoc
    public ResponseEntity<ResponseTaskDto> create(@Valid @RequestBody CreateTaskDto createTaskDto) {
        ResponseTaskDto task = taskService.create(createTaskDto);
        log.info("Task {} created successfully", task.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping
    @UpdateTaskDoc
    public ResponseEntity<ResponseTaskDto> update(@Valid@RequestBody UpdateTaskDto taskDto) {
        ResponseTaskDto task = taskService.update(taskDto);
        log.info("Task {} updated successfully", task.getId());

        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @FiltersTaskDoc
    @GetMapping("/filters/{projectId}")
    public ResponseEntity<List<ResponseTaskDto>> filterTasks(
            @NotNull(message = "ProjectId is required")
            @Positive(message = "ProjectId must be greater than 0.")
            @PathVariable Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @Positive(message = "PerformerUserId must be greater than 0.")
            @RequestParam(required = false) Long performerUserId,
            @RequestParam(required = false) String text
    ) {
        List<ResponseTaskDto> tasks = taskService.filterTasks(
                new TaskFiltersDto(status, text, performerUserId),
                userContext.getUserId(),
                projectId
        );
        log.info("Tasks by project {} filtered successfully", projectId);

        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @GetMapping("/{projectId}")
    @GetTasksDoc
    public ResponseEntity<List<ResponseTaskDto>> getTasks(
            @NotNull(message = "ProjectId is required")
            @Positive(message = "ProjectId must be greater than 0.")
            @PathVariable Long projectId,
            @Positive(message = "Limit must be greater than 0.")
            @RequestParam(required = false) Integer limit,
            @Positive(message = "Offset must be greater than 0.")
            @RequestParam(required = false) Integer offset
    ) {
        List<ResponseTaskDto> tasks = taskService.getTasks(
                userContext.getUserId(),
                projectId,
                limit,
                offset
        );
        log.info("Tasks in project {} getting successfully", projectId);

        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @GetTaskByIdDoc
    @GetMapping("/{projectId}/{taskId}")
    public ResponseEntity<ResponseTaskDto> getTasksById(
            @NotNull(message = "ProjectId is required")
            @Positive(message = "ProjectId must be greater than 0.")
            @PathVariable Long projectId,
            @Positive(message = "TaskId must be greater than 0.")
            @PathVariable Long taskId
    ) {
        ResponseTaskDto task = taskService.getTaskById(
                userContext.getUserId(),
                projectId,
                taskId
        );

        log.info("Task with id {} getting successfully", taskId);

        return ResponseEntity.status(HttpStatus.OK).body(task);
    }
}
