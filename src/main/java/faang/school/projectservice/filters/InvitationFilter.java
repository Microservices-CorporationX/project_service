package faang.school.projectservice.filters;

import faang.school.projectservice.model.stage_invitation.StageInvitation;

public interface InvitationFilter {
    boolean apply(StageInvitation invitation);
}
