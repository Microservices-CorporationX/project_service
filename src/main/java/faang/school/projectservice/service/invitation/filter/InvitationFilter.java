package faang.school.projectservice.service.invitation.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitation;

public interface InvitationFilter {
    boolean matches(StageInvitation invitation);
}
