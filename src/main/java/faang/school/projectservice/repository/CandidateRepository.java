package faang.school.projectservice.repository;

import faang.school.projectservice.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findAllByVacancyId(Long vacancyId);
}
