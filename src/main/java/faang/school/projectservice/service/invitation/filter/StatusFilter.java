package faang.school.projectservice.service.invitation.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StatusFilter implements InvitationFilter {
    private final StageInvitationStatus status;

    @Override
    public boolean matches(StageInvitation invitation) {
        return invitation.getStatus().equals(status);
    }
}
