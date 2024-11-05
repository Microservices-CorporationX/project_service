package faang.school.projectservice.controller.stage_invitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.service.stageInvitation.StageInvitationService;
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

    @PostMapping("/accept/{userId}")
    public void acceptStageInvitation(@PathVariable long userId, @RequestParam long stageInvitationId) {
        stageInvitationService.acceptInvitation(userId, stageInvitationId);
    }

    @PostMapping("/reject/{userId}")
    public void rejectStageInvitation(@PathVariable long userId, @RequestParam long stageInvitationId) {
        stageInvitationService.rejectInvitation(userId, stageInvitationId);
    }

    @GetMapping("/{userId}")
    public StageInvitationDto getStageInvitations(
            @PathVariable long userId, @RequestParam StageInvitationFilterDto filter) {
        return stageInvitationService.getStageInvitations(userId, filter);
    }
}