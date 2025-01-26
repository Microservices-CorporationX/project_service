package faang.school.projectservice.controller;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.CreateTaskResult;
import faang.school.projectservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public CreateTaskResult createTask(@Valid CreateTaskDto createTaskDto) {
        return taskService.createTask(createTaskDto);
    }
}
