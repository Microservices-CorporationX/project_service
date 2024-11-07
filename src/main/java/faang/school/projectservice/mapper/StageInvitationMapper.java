package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface StageInvitationMapper {
    @Mapping(target = "stageId", expression = "java(stageInvitation.getStage().getStageId())")
    @Mapping(target = "authorId", expression = "java(stageInvitation.getAuthor().getId())")
    @Mapping(target = "invitedId", expression = "java(stageInvitation.getInvited().getId())")
    StageInvitationDto toDto(StageInvitation stageInvitation);
}
