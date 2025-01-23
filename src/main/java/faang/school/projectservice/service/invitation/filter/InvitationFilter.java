package faang.school.projectservice.service.invitation.filter;

import faang.school.projectservice.dto.invitation.InvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface InvitationFilter {
    boolean isApplicable(InvitationDto filter);
    Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream, InvitationDto filter);
}

