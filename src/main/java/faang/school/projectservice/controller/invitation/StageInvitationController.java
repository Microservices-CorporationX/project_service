package faang.school.projectservice.controller.invitation;

import faang.school.projectservice.dto.invitation.RejectionReasonDTO;
import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.service.invitation.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stage-invitation")
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageInvitationDTO sendInvitation(@Valid @RequestBody StageInvitationDTO stageInvitationDTO) {
        log.info("Получен запрос на отправку приглашения: {}", stageInvitationDTO);
        return stageInvitationService.sendInvitation(stageInvitationDTO);
    }

    @PatchMapping("/{invitationId}/accept")
    public StageInvitationDTO acceptInvitation(@PathVariable Long invitationId) {
        log.info("Получен запрос на принятие приглашения: {}", invitationId);
        return stageInvitationService.acceptInvitation(invitationId);
    }

    @PatchMapping("/{invitationId}/reject")
    public StageInvitationDTO rejectInvitation(@PathVariable Long invitationId,
                                               @Valid @RequestBody RejectionReasonDTO rejectionReasonDTO) {
        log.info("Получен запрос на отклонение приглашения с ID: {} по причине: {}", invitationId, rejectionReasonDTO.getRejectReason());
        return stageInvitationService.rejectInvitation(invitationId, rejectionReasonDTO.getRejectReason());
    }

    @PostMapping("/filter")
    public List<StageInvitationDTO> getFilteredInvitations(@RequestBody @Valid StageInvitationDTO filterDTO) {
        log.info("Получен запрос на фильтрацию приглашений с фильтром: {}", filterDTO);
        return stageInvitationService.getFilteredInvitations(filterDTO);
    }
}
