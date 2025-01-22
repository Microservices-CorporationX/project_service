package faang.school.projectservice.service;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public List<Task> findTasksByIds(List<Long> taskIds) {
        return taskRepository.findAllById(taskIds);
    }
}
