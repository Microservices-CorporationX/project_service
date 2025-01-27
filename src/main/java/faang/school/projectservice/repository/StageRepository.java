package faang.school.projectservice.repository;

import faang.school.projectservice.model.stage.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StageRepository extends JpaRepository<Stage, Long> {
    @Query(nativeQuery = true, value = """
            SELECT st.* FROM stage st " +
                    "JOIN project_stage_roles pst st.project_stage_id=pst.project_stage_id " +
                    "JOIN task t st.project_stage_id=t.stage_id " +
                    "WHERE pst.teamRole = ?1 " +
                    "AND t.status = ?2
            """)
    List<Stage> getAllStagesByFilter(String role, String status);


//    @Query(nativeQuery = true, value = """
//            UPDATE stage SET project_id = :projectId, updated_at = now()
//            WHERE stage_id = :stageId AND receiver_id = :receiverId
//            """)
//    @Modifying
//    void update(long stageId, long projectId, String content);
}
