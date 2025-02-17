package ru.corporationx.projectservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.corporationx.projectservice.model.entity.Internship;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {
}
