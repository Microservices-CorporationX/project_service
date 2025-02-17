package ru.corporationx.projectservice.mapper.stageinvitation;

import ru.corporationx.projectservice.model.dto.invitation.StageInvitationDto;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    StageInvitationDto toDto(StageInvitation stageInvitation);
}
