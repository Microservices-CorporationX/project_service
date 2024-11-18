package faang.school.projectservice.controller;

import faang.school.projectservice.dto.invitation.RejectionDto;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.abstracts.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage-invitations")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping
    public StageInvitationDto sendStageInvitation(@RequestBody @Valid StageInvitationDto stageInvitationDto) {
        return stageInvitationService.sendStageInvitation(stageInvitationDto);
    }

    @PatchMapping("/{invitationId}/accept")
    public StageInvitationDto acceptStageInvitation(@PathVariable long invitationId) {
        return stageInvitationService.acceptStageInvitation(invitationId);
    }

    @PatchMapping("/{invitationId}/reject")
    public StageInvitationDto rejectStageInvitation(@PathVariable long invitationId,
                                                    @RequestBody @Valid RejectionDto rejectionDto) {
        return stageInvitationService.rejectStageInvitation(invitationId, rejectionDto);
    }

    @PostMapping("/{invitedId}/filter")
    public List<StageInvitationDto> getInvitations(@PathVariable long invitedId,
                                                   @RequestBody StageInvitationFilterDto filterDto) {
        return stageInvitationService.getInvitations(invitedId, filterDto);
    }
}