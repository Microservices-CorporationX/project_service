package faang.school.projectservice.controller.stage_invitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.service.stageInvitation.StageInvitationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("/stage-invitation")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping("/send/{invitorId}")
    public void sendStageInvitation(@PathVariable long invitorId, @RequestParam StageInvitationDto dto) {
        stageInvitationService.sendStageInvitation(invitorId, dto);
    }

    @PostMapping("/accept/{teamMemberId}")
    public void acceptStageInvitation(@PathVariable long teamMemberId, @RequestParam long stageInvitationId) {
        stageInvitationService.acceptStageInvitation(teamMemberId, stageInvitationId);
    }

    @PostMapping("/reject/{teamMemberId}")
    public void rejectStageInvitation(@PathVariable long teamMemberId, @RequestParam long stageInvitationId,
    @RequestParam String rejectReason) {
        stageInvitationService.rejectStageInvitation(teamMemberId, stageInvitationId,rejectReason);
    }

    @GetMapping("/{teamMemberId}")
    public StageInvitationDto getStageInvitations(
            @PathVariable long teamMemberId, @RequestParam StageInvitationFilterDto filter) {
        return stageInvitationService.getStageInvitations(teamMemberId, filter);
    }
}