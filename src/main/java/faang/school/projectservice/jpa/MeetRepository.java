package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetRepository extends JpaRepository<Meet, Long> {

    Optional<Meet> findByProject(Project project);

    Optional<Meet> findByCreatorId(long creatorId);

    Optional<Meet> findByIdAndCreatorId(long id, long creatorId);

    List<Meet> findAllByProjectId(long projectId);
}
