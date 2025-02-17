package ru.corporationx.projectservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.corporationx.projectservice.model.entity.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
}
