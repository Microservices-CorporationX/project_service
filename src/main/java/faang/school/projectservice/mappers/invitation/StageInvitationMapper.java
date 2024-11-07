package faang.school.projectservice.mappers.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StageInvitationMapper {
    StageInvitationDTO toDto(StageInvitation stageInvitation);
    StageInvitation toEntity(StageInvitationDTO stageInvitationDTO);
}
