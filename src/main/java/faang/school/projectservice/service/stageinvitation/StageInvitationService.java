package faang.school.projectservice.service.stageinvitation;

import faang.school.projectservice.dto.RejectionDto;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;

import java.util.List;

public interface StageInvitationService {
    StageInvitationDto sendStageInvitation(StageInvitationDto stageInvitationDto);

    StageInvitationDto acceptStageInvitation(Long invitationId);

    StageInvitationDto rejectStageInvitation(Long invitationId, RejectionDto rejectionDto);

    List<StageInvitationDto> getInvitations(Long invitedId, StageInvitationFilterDto filterDto);
}
