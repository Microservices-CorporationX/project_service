package faang.school.projectservice.mapper.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

public interface StageInvitationMapper {

    StageInvitation toEntity(StageInvitationDTO stageInvitationDTO);

    StageInvitationDTO toDto(StageInvitation stageInvitation);
}
