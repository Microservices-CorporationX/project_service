package faang.school.projectservice.filters;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserIdFilter implements InvitationFilter {
    private final Long userId;

    @Override
    public boolean apply(StageInvitation invitation) {
        return invitation.getInvited() != null && invitation.getInvited().getId().equals(userId);
    }
}
