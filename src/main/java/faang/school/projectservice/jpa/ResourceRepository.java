package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query(
            "SELECT pr " +
                    "FROM Resource pr " +
                    "WHERE pr.project.id = :projectId"
    )
    List<Resource> getResourceByProjectId(Long projectId);
}