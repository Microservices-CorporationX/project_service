package faang.school.projectservice.service.task;

import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public void saveAll(List<Task> taskList) {
        taskRepository.saveAll(taskList);
    }

}
