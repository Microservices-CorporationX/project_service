package ru.corporationx.projectservice.repository.jpa;

import ru.corporationx.projectservice.model.entity.stage.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StageJpaRepository extends JpaRepository<Stage, Long> {
}
