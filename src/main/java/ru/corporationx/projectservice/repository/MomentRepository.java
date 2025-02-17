package ru.corporationx.projectservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.corporationx.projectservice.model.entity.Moment;

import java.util.List;

@Repository
public interface MomentRepository extends JpaRepository<Moment, Long> {

    @Query(nativeQuery = true, value = """
    select m.* from moment m
    where m.id in
    (select moment_id from moment_project where project_id = :projectId)
    """)
    List<Moment> findAllByProjectId(long projectId);
}
