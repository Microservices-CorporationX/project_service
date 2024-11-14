package faang.school.projectservice.controller.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.invitation.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/stage-invitations")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageInvitationDTO sendInvitation(@Valid @RequestBody StageInvitationDTO invitationDto) {
        log.info("Received request to send invitation: {}", invitationDto);
        return stageInvitationService.sendInvitation(invitationDto);
    }

    @PatchMapping("/{invitationId}/accept")
    public StageInvitationDTO acceptInvitation(@PathVariable Long invitationId) {
        log.info("Received request to accept invitation with ID: {}", invitationId);
        return stageInvitationService.acceptInvitation(invitationId);
    }

    @PatchMapping("/{invitationId}/reject")
    public StageInvitationDTO rejectInvitation(@PathVariable Long invitationId,
                                               @RequestBody StageInvitationDTO stageInvitationDTO) {
        log.info("Получен запрос на отклонение приглашения с ID: {} по причине: {}", invitationId, stageInvitationDTO.getRejectionReason());
        return stageInvitationService.rejectInvitation(invitationId, stageInvitationDTO);
    }

    @GetMapping("/users/{userId}")
    public List<StageInvitationDTO> getAllInvitations(@PathVariable Long userId,
                                                      @RequestParam(required = false) StageInvitationStatus status,
                                                      @RequestParam(required = false) LocalDate dateFilter) {
        log.info("Received request to get all invitations for user ID: {}, with status: {}, dateFilter: {}", userId, status, dateFilter);
        return stageInvitationService.getAllInvitationsForUser(userId, status, dateFilter);
    }
}
