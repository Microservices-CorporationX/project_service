package faang.school.projectservice.filters.abstracts;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface StageInvitationFilter {
    boolean isApplicable(StageInvitationFilterDto filterDto);

    Stream<StageInvitation> apply(Stream<StageInvitation> invitationStream,
                                  StageInvitationFilterDto filterDto);
}
