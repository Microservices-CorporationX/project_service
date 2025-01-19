package faang.school.projectservice.validator;

import faang.school.projectservice.dto.stageinvitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final StageInvitationMapper mapper;

    public StageInvitation validateStageInvitation(@NotNull StageInvitationDto dto) {

        StageInvitation stageInvitation = mapper.toEntity(dto);
        String description = stageInvitation.getDescription();

        if (stageInvitation.getStage() == null) {
            throw new DataValidationException("Этап не должен быть null");
        }

        if (description == null) {
            throw new DataValidationException("Описание не должно быть null");
        }

        if (description.isBlank()) {
            throw new DataValidationException("Описание не должно быть пустым");
        }

        return stageInvitation;
    }
}
