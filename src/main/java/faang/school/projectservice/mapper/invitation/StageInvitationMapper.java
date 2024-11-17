package faang.school.projectservice.mapper.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StageInvitationMapper {

    StageInvitation toEntity(StageInvitationDTO stageInvitationDTO);

    StageInvitationDTO toDto(StageInvitation stageInvitation);
}
