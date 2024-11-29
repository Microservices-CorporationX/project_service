package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(Long projectId);

    List<Task> findTasksByProjectId(Long projectId);

    List<Task> findTasksByProjectId(Long projectId, Pageable pageable);
}
