package ru.corporationx.projectservice.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.corporationx.projectservice.model.entity.Resource;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    @Query("SELECT r FROM Resource r WHERE r.project.id = :projectId AND r.status <> 'DELETED'")
    List<Resource> findAllByProjectId(@Param("projectId") Long projectId);
}
