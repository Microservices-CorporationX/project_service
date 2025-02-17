package ru.corporationx.projectservice.service.stageinvitation;

import ru.corporationx.projectservice.model.dto.RejectionDto;
import ru.corporationx.projectservice.model.dto.invitation.StageInvitationDto;
import ru.corporationx.projectservice.model.dto.invitation.StageInvitationFilterDto;

import java.util.List;

public interface StageInvitationService {
    StageInvitationDto sendStageInvitation(StageInvitationDto stageInvitationDto);

    StageInvitationDto acceptStageInvitation(Long invitationId);

    StageInvitationDto rejectStageInvitation(Long invitationId, RejectionDto rejectionDto);

    List<StageInvitationDto> getInvitations(Long invitedId, StageInvitationFilterDto filterDto);
}
