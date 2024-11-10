package faang.school.projectservice.controller.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/stage-invitations")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping("/send/{invitorId}")
    public void sendStageInvitation(@PathVariable long invitorId, @RequestBody @Valid StageInvitationDto dto) {
        stageInvitationService.sendStageInvitation(invitorId, dto);
    }

    @PutMapping("/accept/{teamMemberId}")
    public void acceptStageInvitation(@PathVariable long teamMemberId, @RequestParam long stageInvitationId) {
        stageInvitationService.acceptStageInvitation(teamMemberId, stageInvitationId);
    }

    @PutMapping("/reject/{teamMemberId}")
    public void rejectStageInvitation(@PathVariable long teamMemberId, @RequestParam long stageInvitationId,
                                      @RequestBody String rejectReason) {
        stageInvitationService.rejectStageInvitation(teamMemberId, stageInvitationId, rejectReason);
    }

    @PostMapping("/{teamMemberId}")
    public List<StageInvitationDto> getStageInvitations(
            @PathVariable long teamMemberId, @RequestBody StageInvitationFilterDto filter) {
        return stageInvitationService.getStageInvitations(teamMemberId, filter);
    }
}