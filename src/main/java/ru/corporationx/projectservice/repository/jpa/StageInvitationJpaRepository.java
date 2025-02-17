package ru.corporationx.projectservice.repository.jpa;

import ru.corporationx.projectservice.model.entity.TeamMember;
import ru.corporationx.projectservice.model.entity.stage.Stage;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageInvitationJpaRepository extends JpaRepository<StageInvitation, Long>, JpaSpecificationExecutor<StageInvitation> {

    boolean existsByAuthorAndInvitedAndStage(TeamMember author, TeamMember invited, Stage stage);

    boolean existsByInvitedAndStage(TeamMember invited, Stage stage);

    List<StageInvitation> findStageInvitationsByInvited(TeamMember invited);
}
