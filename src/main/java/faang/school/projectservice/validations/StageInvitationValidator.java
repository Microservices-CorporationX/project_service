package faang.school.projectservice.validations;

import faang.school.projectservice.dto.stage_invitation.RejectStageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final StageService stageService;
    private final TeamMemberService teamMemberService;

    private final List<StageInvitationStatus> statuses = List.of(
            StageInvitationStatus.ACCEPTED,
            StageInvitationStatus.PENDING,
            StageInvitationStatus.REJECTED
    );

    public void validateStageInvitation(StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto.getStageId() != null && !stageService.existsById(stageInvitationDto.getStageId())) {
            throw new EntityNotFoundException("StageId don`t have existing");
        }

        if (stageInvitationDto.getAuthorId() != null && !teamMemberService.existsById(stageInvitationDto.getAuthorId())) {
            throw new EntityNotFoundException("AuthorId don`t have existing");
        }

        if (stageInvitationDto.getInvitedId() != null && !teamMemberService.existsById(stageInvitationDto.getInvitedId())) {
            throw new EntityNotFoundException("InvitedId don`t have existing");
        }

        if (stageInvitationDto.getStatus() != null && !statuses.contains(stageInvitationDto.getStatus())) {
            throw new DataValidationException("Status is not valid. Validation status: " + statuses);
        }
    }
}
