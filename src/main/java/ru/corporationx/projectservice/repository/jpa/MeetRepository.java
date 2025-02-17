package ru.corporationx.projectservice.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.corporationx.projectservice.model.entity.Meet;
import ru.corporationx.projectservice.model.entity.Project;

import java.util.Optional;

@Repository
public interface MeetRepository extends JpaRepository<Meet, Long> {

    Optional<Meet> findByProject(Project project);

    Optional<Meet> findByCreatorId(long creatorId);
}
