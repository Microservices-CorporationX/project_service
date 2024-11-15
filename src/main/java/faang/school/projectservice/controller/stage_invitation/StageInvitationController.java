package faang.school.projectservice.controller.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationRejectDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/stage-invitations")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping("/send")
    public void sendStageInvitation(@RequestBody @Valid StageInvitationDto dto) {
        stageInvitationService.sendStageInvitation(dto);
    }

    @PatchMapping("/accept")
    public void acceptStageInvitation(@RequestBody @Valid StageInvitationDto dto) {
        stageInvitationService.acceptStageInvitation(dto);
    }

    @PatchMapping("/reject")
    public void rejectStageInvitation(@RequestBody @Valid StageInvitationRejectDto rejectDto) {
        stageInvitationService.rejectStageInvitation(rejectDto);
    }

    @PostMapping("/{teamMemberId}")
    public List<StageInvitationDto> getStageInvitations(
            @PathVariable long teamMemberId, @RequestBody StageInvitationFilterDto filter) {
        return stageInvitationService.getStageInvitations(teamMemberId, filter);
    }
}