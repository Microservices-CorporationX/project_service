package faang.school.projectservice.controller;

import faang.school.projectservice.docs.stage_invitation.AcceptStageInvitationDoc;
import faang.school.projectservice.docs.stage_invitation.FiltersStageInvitationDoc;
import faang.school.projectservice.docs.stage_invitation.RejectStageInvitationDoc;
import faang.school.projectservice.docs.stage_invitation.SendStageInvitationDoc;
import faang.school.projectservice.dto.stage_invitation.AcceptStageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.RejectStageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFiltersDto;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.StageInvitationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stage-invitation")
@Tag(name = "Stage invitation", description = "Stage invitation controller")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping
    @SendStageInvitationDoc
    public ResponseEntity<StageInvitationDto> sendStageInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        log.info("Received request to send stage invitation: {}", stageInvitationDto);

        return ResponseEntity.ok(stageInvitationService.sendStageInvitation(stageInvitationDto));
    }

    @PutMapping("/accept")
    @AcceptStageInvitationDoc
    public ResponseEntity<StageInvitationDto> acceptStageInvitation(@Valid @RequestBody AcceptStageInvitationDto stageInvitation) {
        log.info("Received request to accept stage invitation: {}", stageInvitation);

        return ResponseEntity.ok(stageInvitationService.acceptStageInvitation(stageInvitation));
    }

    @PutMapping("/reject")
    @RejectStageInvitationDoc
    public ResponseEntity<StageInvitationDto> rejectStageInvitation(@Valid @RequestBody RejectStageInvitationDto rejectStageInvitationDto) {
        log.info("Received request to reject stage invitation: {}", rejectStageInvitationDto);

        return ResponseEntity.ok(stageInvitationService.rejectStageInvitation(rejectStageInvitationDto));
    }

    @GetMapping("/filters")
    @FiltersStageInvitationDoc
    public ResponseEntity<List<StageInvitationDto>> filterStageInvitation(
            @NotNull(message = "StageId is required")
            @Positive(message = "StageId must be greater than 0.")
            @RequestParam(required = false) Long stageId,
            @NotNull(message = "AuthorId is required")
            @Positive(message = "AuthorId must be greater than 0.")
            @RequestParam(required = false) Long authorId,
            @NotNull(message = "InvitedId is required")
            @Positive(message = "InvitedId must be greater than 0.")
            @RequestParam(required = false) Long invitedId,
            @NotNull(message = "Status is required")
            @RequestParam(required = false) StageInvitationStatus status
    ) {
        StageInvitationFiltersDto filters = new StageInvitationFiltersDto(stageId, authorId, invitedId, status);

        log.info("Received request to filter stage invitation: {}", filters);

        return ResponseEntity.ok(stageInvitationService.filters(filters));
    }
}
