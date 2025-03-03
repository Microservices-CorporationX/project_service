package ru.corporationx.projectservice.validator.stageinvitation;

import ru.corporationx.projectservice.exception.DataValidationException;
import ru.corporationx.projectservice.model.entity.TeamMember;
import ru.corporationx.projectservice.repository.jpa.StageInvitationJpaRepository;
import ru.corporationx.projectservice.model.entity.stage.Stage;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitation;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitationStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StageInvitationValidatorImpl implements StageInvitationValidator {
    private final StageInvitationJpaRepository stageInvitationRepository;

    @Override
    public StageInvitation getStageInvitation(Long invitationId) {
        return stageInvitationRepository.findById(invitationId).orElseThrow(() -> {
            log.error("Stage invitation doesn't exist by id: {}", invitationId);
            return new EntityNotFoundException("Stage invitation doesn't exist");
        });
    }

    @Override
    public void validateAuthorAndInvited(Long authorId, Long invitedId) {
        if (authorId.equals(invitedId)) {
            log.error("user sent the same authorId and invitedId: {}", authorId);
            throw new DataValidationException("authorId and invitedId must be different");
        }
    }

    @Override
    public void validateInvitationDoesNotExist(TeamMember invited, Stage stage) {
        if (stageInvitationRepository.existsByInvitedAndStage(invited, stage)) {
            log.error("invitation with invitedId: {}; stageId: {} already exists",
                    invited.getId(), stage.getStageId());
            throw new DataValidationException("This invitation already exists");
        }
    }

    @Override
    public void validateInvitationStatus(StageInvitation stageInvitation, StageInvitationStatus requiredStatus) {
        if (stageInvitation.getStatus().equals(requiredStatus)) {
            String requiredStatusString = requiredStatus.name().toLowerCase();
            log.error("Invitation with id: {} has already been {}", stageInvitation.getId(), requiredStatusString);
            throw new DataValidationException(String.format("Invitation has already been %s", requiredStatusString));
        }
    }
}
