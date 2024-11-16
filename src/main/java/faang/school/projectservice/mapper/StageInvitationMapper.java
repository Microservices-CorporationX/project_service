package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.invitation.StageInvitationRequestDto;
import faang.school.projectservice.dto.invitation.StageInvitationResponseDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "Spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    StageInvitationResponseDto toRsDto(StageInvitation stageInvitation);

    StageInvitationRequestDto toRqDto(StageInvitation stageInvitation);

    StageInvitation toRsEntity(StageInvitationResponseDto stageInvitationResponseDto);

    StageInvitation toRqEntity(StageInvitationRequestDto stageInvitationRequestDto);
}
