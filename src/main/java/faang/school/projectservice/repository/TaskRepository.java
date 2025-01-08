package faang.school.projectservice.repository;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.jpa.TaskJpaRepository;
import faang.school.projectservice.model.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TaskRepository {
    private final TaskJpaRepository taskJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Task save(String name, String description, String status, Long reporterUserId,
                     Long performerUserId, Long parentTaskId, Long projectId, Long stageId) {
        return taskJpaRepository.save(name, description, status, reporterUserId,
                performerUserId, parentTaskId, projectId, stageId);
    }

    public void linkTask(Long taskId, Long linkedTaskId) {
        taskJpaRepository.linkTask(taskId, linkedTaskId);
    }

    public void unlinkTask(Long taskId) {
        taskJpaRepository.unlinkTask(taskId);
    }

    public void update(Long taskId, String description, String status,
                       Integer minutesTracked, Long reporterUserId, Long parentTaskId) {
        taskJpaRepository.updateTask(taskId, description, status, minutesTracked,
                reporterUserId, parentTaskId);
    }

    public Task getById(Long taskId) {
        return taskJpaRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                        "Task not found by id: %s", taskId)));
    }

    public List<Task> findAll() {
        return taskJpaRepository.findAll();
    }

    public List<Task> findAllByProjectId(Long projectId) {
        return taskJpaRepository.findAllByProjectId(projectId);
    }

    public List<Task> findByFilter(TaskFilterDto taskFilterDto) {
        StringBuilder queryStr = new StringBuilder("SELECT * FROM Task  WHERE 1=1");

        if (taskFilterDto.getName() != null) {
            queryStr.append(" AND name = \'" + taskFilterDto.getName() + "\'");
        }
        if (taskFilterDto.getStatus() != null) {
            queryStr.append(" AND status = \'" + taskFilterDto.getStatus() + "\'");
        }
        if (taskFilterDto.getReporterUserId() != null) {
            queryStr.append(" AND reporter_user_id = " + taskFilterDto.getReporterUserId());
        }
        Query query = entityManager.createNativeQuery(queryStr.toString(), Task.class);

        return query.getResultList();
    }

    public void saveAll(List<Task> tasks) {
        taskJpaRepository.saveAll(tasks);
    }

    public void delete(Long taskId) {
        taskJpaRepository.deleteById(taskId);
    }


}
