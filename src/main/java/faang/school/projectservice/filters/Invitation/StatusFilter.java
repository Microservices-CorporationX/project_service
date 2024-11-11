package faang.school.projectservice.filters.Invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StatusFilter implements InvitationFilter {
    private final StageInvitationStatus status;

    @Override
    public boolean apply(StageInvitation invitation) {
        return invitation.getStatus().equals(status);
    }
}
