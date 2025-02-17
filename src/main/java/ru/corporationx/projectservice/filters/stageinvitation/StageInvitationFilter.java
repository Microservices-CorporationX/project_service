package ru.corporationx.projectservice.filters.stageinvitation;

import ru.corporationx.projectservice.model.dto.invitation.StageInvitationFilterDto;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface StageInvitationFilter {
    boolean isApplicable(StageInvitationFilterDto filterDto);

    Stream<StageInvitation> apply(Stream<StageInvitation> invitationStream,
                                  StageInvitationFilterDto filterDto);
}
