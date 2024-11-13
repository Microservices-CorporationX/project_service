package faang.school.projectservice.filters.Invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitation;

public interface InvitationFilter {
    boolean matches(StageInvitation invitation);
}
