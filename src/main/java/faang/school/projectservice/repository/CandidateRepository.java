package faang.school.projectservice.repository;

import faang.school.projectservice.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    @Query("SELECT c FROM Candidate c WHERE c.vacancy.id = :vacancyId")
    List<Candidate> findAllByVacancyId(@Param("vacancyId") Long vacancyId);

    void deleteByUserIdIn(List<Long> ids);
}
