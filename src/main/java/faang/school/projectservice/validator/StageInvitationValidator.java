package faang.school.projectservice.validator;

import faang.school.projectservice.exception.AlreadyExistsException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.IllegalArgumentException;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {

    private final StageInvitationJpaRepository repository;

    public void validateStageInvitationExists(long id) {
        repository.findById(id).orElseThrow(
                () -> new AlreadyExistsException("Stage invitation with id: " + id + " already exists"));
    }

    public StageInvitation validateStageInvitationNotExists(long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Stage invitation with id: " + id + " not exists"));
    }

    public void validateIsInvitationSentToThisTeamMember(long invitedId, long stageInvitationId) {
        StageInvitation stageInvitation = validateStageInvitationNotExists(stageInvitationId);

        if (!stageInvitation.getInvited().getId().equals(invitedId)) {
            throw new IllegalArgumentException("This stage invitation does not belong to this team member");
        }
    }
}
