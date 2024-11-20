package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeetRepository extends JpaRepository<Meet, Long> {

    Optional<Meet> findByProject(Project project);

    Optional<Meet> findByCreatorId(long creatorId);

    List<Meet> findByProjectId(long projectId);

    List<Meet> findByProjectIdAndTitleContainingIgnoreCase(Long projectId, String title);

    List<Meet> findByProjectIdAndCreatedAtAfter(Long projectId, LocalDateTime date);

    List<Meet> findByProjectIdAndTitleContainingIgnoreCaseAndCreatedAtAfter(Long projectId, String title, LocalDateTime date);
}
