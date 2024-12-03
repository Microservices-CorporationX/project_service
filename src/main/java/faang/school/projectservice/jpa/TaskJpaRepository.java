package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskJpaRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(Long projectId);
}
