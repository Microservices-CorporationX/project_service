package faang.school.projectservice.mapper.stageinvitation;

import faang.school.projectservice.dto.stageinvitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    @Mapping(target = "stage", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "invited", ignore = true)
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    StageInvitationDto toDto(StageInvitation stageInvitation);
}
