package faang.school.projectservice.repository;

import faang.school.projectservice.model.Campaign;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    Optional<Campaign> findByTitleAndProjectId(String title, Long projectId);

    default Campaign findByIdOrThrow(Long campaignId) {
        return this.findById(campaignId).orElseThrow(
                () -> new EntityNotFoundException("Campaign with id " + campaignId + " not found"));
    }

    @Query("""
    FROM Campaign c
    WHERE (c.project.id = :projectId)
    AND (:createdBy IS NULL OR c.createdBy = :createdBy)
    AND (:namePattern IS NULL OR c.title LIKE %:namePattern%)
    AND (:minGoal IS NULL OR c.goal >= :minGoal)
    AND (:maxGoal IS NULL OR c.goal <= :maxGoal)
    AND (:status IS NULL OR c.status = :status)
    AND (:startDate IS NULL OR c.createdAt >= :createdAfter)
    ORDER BY c.createdAt DESC""")
    List<Campaign> findAllByFilters(Long projectId,
                                    Long createdById,
                                    String namePattern,
                                    BigDecimal minGoal,
                                    BigDecimal maxGoal,
                                    String status,
                                    LocalDate createdAfter,
                                    Pageable pageable);

    @Query("""
    FROM Campaign c
    WHERE c.project.id = :projectId
    ORDER BY c.createdAt DESC""")
    List<Campaign> findAllByProjectId(Long projectId, Pageable pageable);
}
