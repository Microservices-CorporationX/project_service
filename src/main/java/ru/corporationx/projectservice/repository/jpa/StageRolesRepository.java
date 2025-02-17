package ru.corporationx.projectservice.repository.jpa;

import ru.corporationx.projectservice.model.entity.stage.StageRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StageRolesRepository extends JpaRepository<StageRoles, Long> {
}
