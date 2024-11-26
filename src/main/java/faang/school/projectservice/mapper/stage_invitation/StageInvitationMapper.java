package faang.school.projectservice.mapper.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    @Mapping(source = "stage.id", target = "stageId")
    StageInvitationDto toDto(StageInvitation stageInvitation);

    @Mapping(source = "authorId", target = "author.id", qualifiedByName = "toInvitedId")
    @Mapping(source = "invitedId", target = "invited.id", qualifiedByName = "toIdToTeamMember")
    @Mapping(source = "stageId", target = "stage.id", qualifiedByName = "toIdToTeamMember")
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

    @Named("toIdToStage")
    default Stage mapStageToStageId(Long stageId) {
        if (stageId == null) {
            return null;
        }
        Stage stage = new Stage();
        stage.setStageId(stageId);
        return stage;
    }

    @Named("toIdToTeamMember")
    default TeamMember mapToTeamMemberId(Long authorId) {
        if (authorId == null) {
            return null;
        }
        TeamMember teamMember = new TeamMember();
        teamMember.setId(authorId);
        return teamMember;
    }

    @Named("toInvitedId")
    default TeamMember mapStageToInvitedId(Long invitedId) {
        if (invitedId == null) {
            return null;
        }
        TeamMember teamMember = new TeamMember();
        teamMember.setId(invitedId);
        return teamMember;
    }
}
