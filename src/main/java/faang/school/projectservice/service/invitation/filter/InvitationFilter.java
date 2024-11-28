package faang.school.projectservice.service.invitation.filter;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface InvitationFilter {
    boolean isApplicable(StageInvitationDTO filter);

    Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream, StageInvitationDTO filter);
}
