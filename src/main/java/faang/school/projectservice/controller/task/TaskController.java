package faang.school.projectservice.controller.task;

import faang.school.projectservice.service.task.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "API for managing projects' tasks")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
@Validated
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/{taskId}/jira/{jiraDomain}")
    ResponseEntity<Void> createJiraTask(
            @PathVariable @Min(1) long taskId,
            @PathVariable @NotBlank @Size(max = 64) String jiraDomain
    ) {
        return null;
    }
}
