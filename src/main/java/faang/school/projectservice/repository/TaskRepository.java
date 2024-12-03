package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.TaskJpaRepository;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TaskRepository {
    private final TaskJpaRepository taskJpaRepository;

    public Task create(Long userId, Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setStatus(TaskStatus.TODO);
        task.setPerformerUserId(userId);

        return taskJpaRepository.save(task);
    }

    public Task update(Long userId, Task task) {
        task.setUpdatedAt(LocalDateTime.now());
        task.setReporterUserId(userId);

        return taskJpaRepository.save(task);
    }

    public Task getById(Long taskId) {
        return taskJpaRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                        "Task not found by id: %s", taskId)));
    }

    public List<Task> findAll() {
        return taskJpaRepository.findAll();
    }

    public Task save(Task task) {
        return taskJpaRepository.save(task);
    }

    public void saveAll(List<Task> tasks) {
        taskJpaRepository.saveAll(tasks);
    }

    public void delete(Long taskId) {
        taskJpaRepository.deleteById(taskId);
    }


}
