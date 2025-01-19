package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    public void createStageInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        stageInvitationService.createStageInvitation(stageInvitationDto);
    }

    public void acceptStageInvitation(@Valid Long stageInvitationId) {
        stageInvitationService.acceptStageInvitation(stageInvitationId);
    }

    public void rejectStageInvitation(@Valid Long participantId, @RequestBody String rejectionReason) {
        stageInvitationService.rejectStageInvitation(participantId, rejectionReason);
    }

    public void getAllInvitationsForOneParticipant(@Valid Long participantId,
                                                   @RequestBody StageInvitationFilterDto filter) {
        stageInvitationService.getAllInvitationsForOneParticipant(participantId, filter);
    }
}
