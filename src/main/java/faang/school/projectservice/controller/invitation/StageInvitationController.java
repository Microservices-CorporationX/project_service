package faang.school.projectservice.controller.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.service.invitation.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public StageInvitationDTO sendInvitation(@RequestBody StageInvitationDTO invitationDto) {
        return stageInvitationService.sendInvitation(invitationDto);
    }

    @PostMapping("/accept")
    public StageInvitationDTO acceptInvitation(@RequestParam Long invitationId) {
        return stageInvitationService.acceptInvitation(invitationId);
    }

    @PostMapping("/reject")
    public StageInvitationDTO rejectInvitation(
        @RequestParam Long invitationId,
        @RequestParam String rejectionReason) {
        return stageInvitationService.rejectInvitation(invitationId, rejectionReason);
    }

    @GetMapping("/all")
    public List<StageInvitationDTO> getAllInvitations(@RequestParam Long userId) {
        return stageInvitationService.getAllInvitationsForUser(userId);
    }
}
