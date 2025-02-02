package faang.school.projectservice.validator.stageinvitation;

import faang.school.projectservice.dto.stageinvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageinvitation.StageInvitationUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stageinvitation.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final StageInvitationMapper stageInvitationMapper;

    public StageInvitation validateStageInvitation(@NotNull StageInvitationDto dto) {

        validateDescriptionAndStage(dto.getStageId(), dto.getDescription());

        if (dto.getAuthorId() == null) {
            throw new DataValidationException("Id автора не должен быть null");
        }

        if (dto.getInvitedId() == null) {
            throw new DataValidationException("Id приглашаемого не должен быть null");
        }

        return stageInvitationMapper.toEntity(dto);
    }

    public void validateUpdateInvitation(StageInvitationUpdateDto dto) {
        validateDescriptionAndStage(dto.getId(), dto.getDescription());
    }

    private void validateDescriptionAndStage(Long stageId, String description) {
        if (description.isBlank()) {
            throw new DataValidationException("Описание не должно быть пустым");
        }
        if (stageId == null) {
            throw new DataValidationException("Id этапа не должен быть null");
        }
    }
}
