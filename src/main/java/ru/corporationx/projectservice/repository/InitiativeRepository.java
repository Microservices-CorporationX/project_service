package ru.corporationx.projectservice.repository;

import ru.corporationx.projectservice.model.entity.initiative.Initiative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InitiativeRepository extends JpaRepository<Initiative, Long> {
}
