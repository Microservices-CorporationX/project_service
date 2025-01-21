package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stageinvitation.ChangeStatusDto;
import faang.school.projectservice.dto.stageinvitation.RejectInvitationDto;
import faang.school.projectservice.dto.stageinvitation.StageInvitationDto;

import faang.school.projectservice.dto.stageinvitation.StageInvitationUpdateDto;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    public StageInvitationDto createStageInvitation(StageInvitationDto dto) {
        return stageInvitationService.createStageInvitation(dto);
    }

    public StageInvitationUpdateDto updateStageInvitation(StageInvitationUpdateDto dto) {
        return stageInvitationService.updateStageInvitation(dto);
    }

    public RejectInvitationDto rejectStageInvitation(RejectInvitationDto dto) {
        return stageInvitationService.rejectStageInvitation(dto);
    }

    public ChangeStatusDto acceptStageInvitation(ChangeStatusDto dto) {
        return stageInvitationService.acceptStageInvitation(dto);
    }

    public List<StageInvitationDto> getStageInvitationForTeamMember(Long invitedId) {
        return stageInvitationService.getStageInvitationForTeamMember(invitedId);
    }
}
