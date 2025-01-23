package faang.school.projectservice.validator.stageinvitation;

import faang.school.projectservice.dto.stageinvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageinvitation.StageInvitationUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stageinvitation.InvitationUpdateMapper;
import faang.school.projectservice.mapper.stageinvitation.StageInvitationMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final StageInvitationMapper stageInvitationMapper;
    private final InvitationUpdateMapper invitationUpdateMapper;

    public StageInvitation validateStageInvitation(@NotNull StageInvitationDto dto) {

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(dto);
        validateDescriptionAndStage(stageInvitation.getStage(), stageInvitation.getDescription());

        if (stageInvitation.getAuthor() == null) {
            throw new DataValidationException("Id автора не должен быть null");
        }

        if (stageInvitation.getInvited() == null) {
            throw new DataValidationException("Id приглашаемого не должен быть null");
        }

        return stageInvitation;
    }

    public void validateUpdateInvitation(StageInvitationUpdateDto dto) {
        StageInvitation stageInvitation = invitationUpdateMapper.toEntity(dto);
        validateDescriptionAndStage(stageInvitation.getStage(), stageInvitation.getDescription());
    }

    private void validateDescriptionAndStage(Stage stage, String description) {
        if (description.isBlank()) {
            throw new DataValidationException("Описание не должно быть пустым");
        }
        if (stage == null) {
            throw new DataValidationException("Этап не должен быть null");
        }
    }
}
