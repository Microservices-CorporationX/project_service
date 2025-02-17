package ru.corporationx.projectservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.corporationx.projectservice.model.entity.Vacancy;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
}
