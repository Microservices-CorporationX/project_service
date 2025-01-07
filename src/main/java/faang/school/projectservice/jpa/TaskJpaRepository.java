package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskJpaRepository extends JpaRepository<Task, Long> {

    @Query(value = """
                INSERT INTO task 
                (name, description, status, performer_user_id, reporter_user_id,  
                 parent_task_id, project_id, stage_id, created_at, updated_at) 
                VALUES (:name, :description, :status, :performerUserId, 1, :parentTaskId,
                        :projectId, :stageId, NOW(), NOW()) 
                RETURNING * 
            """, nativeQuery = true)
    Task createTask(
            @Param("name") String name,
            @Param("description") String description,
            @Param("status") String status,
            @Param("performerUserId") Long performerUserId,
            @Param("parentTaskId") Long parentTaskId,
            @Param("projectId") Long projectId,
            @Param("stageId") Long stageId
    );

    @Modifying
    @Query(value = """
            INSERT INTO task_linked_tasks (task_id, linked_task_id) 
            VALUES (:taskId, :linkedTaskId)
            """, nativeQuery = true)
    void linkTask(@Param("taskId") Long taskId, @Param("linkedTaskId") Long linkedTaskId);

    List<Task> findAllByProjectId(Long projectId);
}
