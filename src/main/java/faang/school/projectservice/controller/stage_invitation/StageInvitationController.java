package faang.school.projectservice.controller.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stageinvitation")
@Validated
public class StageInvitationController {
    private final StageInvitationService invitationService;

    @PostMapping
    public StageInvitationDto create(@RequestBody @Valid StageInvitationDto stageInvitationDto) {
        return invitationService.create(stageInvitationDto);
    }

    @PostMapping("accept-invitation")
    public StageInvitationDto acceptInvitation(@RequestParam @NotNull Long invitationId,
                                               @RequestParam @NotNull Long invitedId) {
        return invitationService.acceptInvitation(invitationId, invitedId);
    }

    @PostMapping("/reject-invitation")
    public StageInvitationDto rejectInvitation(@RequestParam @NotNull Long invitationId,
                                               @RequestParam @NotNull Long invitedId,
                                               @RequestParam @NotNull String rejectDescription) {
        return invitationService.rejectInvitation(invitationId, invitedId, rejectDescription);
    }

    @GetMapping("/filter-invitation")
    public List<StageInvitationDto> checkAllInvitation(@RequestParam @NotNull Long invitedId,
                                                       @RequestBody @Valid StageInvitationFilterDto filters) {
        return invitationService.checkAllInvitation(invitedId, filters);
    }
}
