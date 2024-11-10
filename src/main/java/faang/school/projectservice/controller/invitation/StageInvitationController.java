package faang.school.projectservice.controller.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.service.invitation.StageInvitationService;
import jakarta.validation.Valid;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageInvitationDTO sendInvitation(@Valid @RequestBody StageInvitationDTO invitationDto) {
        log.info("Запрос на отправку приглашения с данными: {}", invitationDto);
        StageInvitationDTO result = stageInvitationService.sendInvitation(invitationDto);
        log.info("Приглашение успешно отправлено: {}", result);
        return result;
    }

    @PatchMapping("/{invitationId}/accept")
    public StageInvitationDTO acceptInvitation(@PathVariable Long invitationId) {
        log.info("Запрос на принятие приглашения с ID: {}", invitationId);
        StageInvitationDTO result = stageInvitationService.acceptInvitation(invitationId);
        log.info("Приглашение с ID {} успешно принято", invitationId);
        return result;
    }

    @PatchMapping("/{invitationId}/reject")
    public StageInvitationDTO rejectInvitation(
        @PathVariable Long invitationId,
        @RequestBody String rejectionReason) {  // Причина отклонения передается как строка
        log.info("Запрос на отклонение приглашения с ID: {}. Причина: {}", invitationId, rejectionReason);
        StageInvitationDTO result = stageInvitationService.rejectInvitation(invitationId, rejectionReason);
        log.info("Приглашение с ID {} успешно отклонено. Причина: {}", invitationId, rejectionReason);
        return result;
    }

    @GetMapping
    public List<StageInvitationDTO> getAllInvitations(@RequestParam Long userId, @RequestParam String status, @RequestParam String dateFilter) {
        log.info("Запрос на получение всех приглашений для пользователя с ID: {}. Статус: {}, Дата: {}", userId, status, dateFilter);

        List<StageInvitationDTO> invitations = stageInvitationService.getAllInvitationsForUser(userId, status, dateFilter);

        log.info("Получено {} приглашений для пользователя с ID: {}", invitations.size(), userId);
        return invitations;
    }
}
