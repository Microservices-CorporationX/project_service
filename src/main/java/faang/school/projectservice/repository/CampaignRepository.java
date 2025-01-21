package faang.school.projectservice.repository;

import faang.school.projectservice.dto.campaign.FilterCampaignDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    Optional<Campaign> findByTitleAndProjectId(String title, Long projectId);

    @Query(
            "SELECT c FROM Campaign c " +
                    "WHERE (:namePattern IS NULL OR c.title LIKE %:namePattern%) " +
                    "AND (:minGoal IS NULL OR c.goal >= :minGoal) " +
                    "AND (:maxGoal IS NULL OR c.goal <= :maxGoal) " +
                    "AND (:status IS NULL OR c.status = :status)"
    )
    List<Campaign> findAllByFilters(@Param("namePattern") String namePattern,
                                    @Param("minGoal") BigDecimal minGoal,
                                    @Param("maxGoal") BigDecimal maxGoal,
                                    @Param("status") String status,
                                    Pageable pageable);

    @Query(value = """
            UPDATE campaign
            SET deleted = TRUE
            WHERE id = ?1
            """, nativeQuery = true)
    @Modifying
    void markAsDeleted(long campaignId);

    @Query(value = """
            SELECT c
            FROM Campaign c
            WHERE (:#{#dto.createdAt} IS NULL OR c.createdAt = :#{#dto.createdAt})
            AND (:#{#dto.status?.name()} IS NULL OR c.status = :#{#dto.status})
            AND (:#{#dto.createdBy} IS NULL OR c.createdBy = :#{#dto.createdBy})
            AND (c.deleted = false)
            ORDER BY c.createdAt DESC
            """)
    List<Campaign> getFilteredCampaigns(@Param("dto") FilterCampaignDto filterCampaignDto);
}
