package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query(
            "SELECT pr " +
                    "FROM Resource pr " +
                    "WHERE pr.project.id = :projectId"
    )
    List<Resource> getResourceByProjectId(Long projectId);

    @Query("SELECT r.key FROM Resource r WHERE r.id = ?1")
    Optional<String> getResourceKeyById(long resourceId);
}
