package faang.school.projectservice.controller.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.service.invitation.StageInvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public StageInvitationDTO sendInvitation(@RequestBody StageInvitationDTO invitationDto) {
        log.info("Запрос на отправку приглашения с данными: {}", invitationDto);
        StageInvitationDTO result = stageInvitationService.sendInvitation(invitationDto);
        log.info("Приглашение успешно отправлено: {}", result);
        return result;
    }

    @PostMapping("/accept")
    public StageInvitationDTO acceptInvitation(@RequestParam Long invitationId) {
        log.info("Запрос на принятие приглашения с ID: {}", invitationId);
        StageInvitationDTO result = stageInvitationService.acceptInvitation(invitationId);
        log.info("Приглашение с ID {} успешно принято", invitationId);
        return result;
    }

    @PostMapping("/reject")
    public StageInvitationDTO rejectInvitation(
        @RequestParam Long invitationId,
        @RequestParam String rejectionReason) {
        log.info("Запрос на отклонение приглашения с ID: {}. Причина: {}", invitationId, rejectionReason);
        StageInvitationDTO result = stageInvitationService.rejectInvitation(invitationId, rejectionReason);
        log.info("Приглашение с ID {} успешно отклонено. Причина: {}", invitationId, rejectionReason);
        return result;
    }

    @GetMapping("/all")
    public List<StageInvitationDTO> getAllInvitations(@RequestParam Long userId) {
        log.info("Запрос на получение всех приглашений для пользователя с ID: {}", userId);
        List<StageInvitationDTO> invitations = stageInvitationService.getAllInvitationsForUser(userId);
        log.info("Получено {} приглашений для пользователя с ID: {}", invitations.size(), userId);
        return invitations;
    }
}
